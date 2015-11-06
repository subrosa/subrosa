package com.subrosagames.subrosa.domain.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.api.dto.AccountDescriptor;
import com.subrosagames.subrosa.domain.BaseDomainObjectFactory;
import com.subrosagames.subrosa.domain.DomainObjectFactory;
import com.subrosagames.subrosa.domain.file.FileAsset;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.image.repository.ImageRepository;
import com.subrosagames.subrosa.domain.token.TokenFactory;
import com.subrosagames.subrosa.security.PasswordUtility;

/**
 * Factory for account objects.
 */
@Component
public class AccountFactory extends BaseDomainObjectFactory implements DomainObjectFactory<Account> {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private TokenFactory tokenFactory;
    @Autowired
    private PasswordUtility passwordUtility;

    /**
     * Get paginated list of accounts.
     *
     * @param limit      number of accounts
     * @param offset     offset into accounts
     * @param expansions fields to expand
     * @return paginated list of accounts
     */
    public Page<Account> getAccounts(Integer limit, Integer offset, String... expansions) {
        return accountRepository.findAll(null, new PageRequest(0, limit), expansions);
    }

    /**
     * Get account with specified id.
     *
     * @param id         account id
     * @param expansions fields to expand
     * @return account
     * @throws AccountNotFoundException if account with specified id does not exist
     */
    public Account getAccount(int id, String... expansions) throws AccountNotFoundException {
        return accountRepository.findOne(id, expansions)
                .map(this::injectDependencies)
                .orElseThrow(() -> new AccountNotFoundException("No account with id " + id));
    }

    /**
     * Get account with the specified email address.
     *
     * @param email      email
     * @param expansions fields to expand
     * @return account
     * @throws AccountNotFoundException if account with specified email does not exist
     */
    public Account getAccount(String email, String... expansions) throws AccountNotFoundException {
        return accountRepository.findOneByEmail(email, expansions)
                .map(this::injectDependencies)
                .orElseThrow(() -> new AccountNotFoundException("No account with email " + email));
    }

    /**
     * Create account object for account descriptor.
     *
     * @param accountDescriptor account descriptor
     * @return account object
     */
    public Account forDto(AccountDescriptor accountDescriptor) {
        Account account = new Account();
        copyProperties(accountDescriptor, account);
        injectDependencies(account);
        return account;
    }

    /**
     * Inject account object with its dependencies.
     *
     * @param account account
     */
    public Account injectDependencies(Account account) {
        account.setAccountFactory(this);
        account.setAccountRepository(accountRepository);
        account.setImageRepository(imageRepository);
        account.setPasswordUtility(passwordUtility);
        account.setTokenFactory(tokenFactory);
        return account;
    }

    /**
     * Create an account image for the given file asset.
     *
     * @param account   account
     * @param fileAsset file asset
     * @return account image
     */
    public Image imageForFileAsset(Account account, FileAsset fileAsset) {
        Image image = new Image();
        image.setFileAsset(fileAsset);
        image.setAccount(account);
        return image;
    }

}
