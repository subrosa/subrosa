package com.subrosagames.gateway;

import java.util.Locale;
import java.util.Map;
import javax.security.auth.login.AccountNotFoundException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;
import com.subrosagames.gateway.auth.Account;
import com.subrosagames.gateway.auth.AccountActivation;
import com.subrosagames.gateway.auth.AccountRepository;
import com.subrosagames.gateway.auth.ConflictException;
import com.subrosagames.gateway.auth.RegistrationRequest;
import com.subrosagames.gateway.auth.SubrosaPasswordEncoder;
import com.subrosagames.gateway.token.TokenInvalidException;
import com.subrosagames.gateway.token.TokenRepository;
import com.subrosagames.subrosa.api.notification.GeneralCode;
import com.subrosagames.subrosa.api.notification.Notification;
import com.subrosagames.subrosa.api.notification.NotificationConstraint;
import com.subrosagames.subrosa.api.notification.NotificationList;
import com.subrosagames.subrosa.api.notification.Severity;

@RestController
public class RegistrationController {

    private static final Logger LOG = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private SubrosaPasswordEncoder passwordEncoder;

    /**
     * Create an {@link Account} from the provided parameters.
     *
     * @param registrationRequest the registration parameters.
     * @return registered account
     */
    @RequestMapping(value = { "/account", "/account/" }, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public Account register(@RequestBody(required = false) @Valid RegistrationRequest registrationRequest) throws ConflictException {
        Account account = Account.builder()
                .accountRepository(accountRepository)
                .passwordEncoder(passwordEncoder)
                .email(registrationRequest.getEmail())
                .phone(registrationRequest.getPhone())
                .activated(false)
                .build();
        return account.create(registrationRequest.getPassword());
    }

    /**
     * Activate the specified account with the given activation details.
     *
     * @param accountId         account id
     * @param accountActivation activation details
     * @return activated account
     * @throws AccountNotFoundException if account does not exist
     */
    @RequestMapping(value = { "/account/{accountId}/activate", "/account/{accountId}/activate/" }, method = RequestMethod.POST)
    public Account activate(@PathVariable("accountId") Integer accountId,
                            @RequestBody(required = false) AccountActivation accountActivation)
            throws AccountNotFoundException, TokenInvalidException
    {
        LOG.debug("Activating account with ID {} using token {}", accountId, accountActivation);
        if (accountActivation == null || accountActivation.getToken() == null) {
            throw new TokenInvalidException("No POST body supplied");
        }
        if (accountActivation.getToken() == null) {
            throw new TokenInvalidException("No activation token supplied");
        }
        Account account = accountRepository.findOne(accountId);
        account.activate(accountActivation.getToken());
        return account;
    }

    /**
     * Handle {@link TokenInvalidException}.
     *
     * @param e exception
     * @return notification list
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public NotificationList handleTokenInvalidException(TokenInvalidException e) {
        LOG.debug("Global exception handler: {}", e.getMessage());
        Notification notification = new Notification(
                GeneralCode.BAD_REQUEST, Severity.ERROR,
                "Token is not valid");
        Map<Notification.DetailKey, String> details = Maps.newEnumMap(Notification.DetailKey.class);
        details.put(Notification.DetailKey.FIELD, "token");
        details.put(Notification.DetailKey.CONSTRAINT, NotificationConstraint.INVALID.getText());
        notification.setDetails(details);
        return new NotificationList(notification);
    }


    /**
     * Handle {@link ConflictException}.
     *
     * @param e exception
     * @return notification list
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public NotificationList handleConflictException(ConflictException e) {
        LOG.debug("Conflict exception handler: {}", e.getMessage());
        Notification notification = new Notification(
                GeneralCode.INVALID_FIELD_VALUE, Severity.ERROR,
                GeneralCode.INVALID_FIELD_VALUE.getDefaultMessage());
        Map<Notification.DetailKey, String> details = Maps.newEnumMap(Notification.DetailKey.class);
        details.put(Notification.DetailKey.FIELD, e.getField());
        details.put(Notification.DetailKey.CONSTRAINT, NotificationConstraint.UNIQUE.getText());
        notification.setDetails(details);
        return new NotificationList(notification);
    }

    /**
     * Handle {@link AccountNotFoundException}.
     *
     * @param e exception
     * @return notification list
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public NotificationList handleAccountNotFoundException(AccountNotFoundException e) {
        Notification notification = new Notification(
                GeneralCode.DOMAIN_OBJECT_NOT_FOUND, Severity.ERROR,
                e.getMessage());
        return new NotificationList(notification);
    }

    /**
     * Handle {@link MethodArgumentNotValidException}.
     *
     * @param e exception
     * @return notification list
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public NotificationList handleMethodArgumentNotValidException(MethodArgumentNotValidException e, Locale locale) {
        LOG.debug("handleMethodArgumentNotValidException: {}", e.getMessage());
        NotificationList list = new NotificationList();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            Notification notification = new Notification(
                    GeneralCode.INVALID_FIELD_VALUE, Severity.ERROR,
                    GeneralCode.INVALID_FIELD_VALUE.getDefaultMessage());
            Map<Notification.DetailKey, String> details = Maps.newEnumMap(Notification.DetailKey.class);
            details.put(Notification.DetailKey.FIELD, error.getField());
            details.put(Notification.DetailKey.CONSTRAINT, error.getDefaultMessage());
            notification.setDetails(details);
            list.addNotification(notification);
        }
        return list;
    }

}
