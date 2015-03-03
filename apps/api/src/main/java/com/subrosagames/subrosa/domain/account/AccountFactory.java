package com.subrosagames.subrosa.domain.account;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.subrosagames.subrosa.api.dto.AccountDescriptor;
import com.subrosagames.subrosa.domain.BaseDomainObjectFactory;
import com.subrosagames.subrosa.domain.DomainObjectFactory;
import com.subrosagames.subrosa.domain.file.FileAsset;
import com.subrosagames.subrosa.domain.image.Image;
import com.subrosagames.subrosa.domain.token.TokenFactory;
import com.subrosagames.subrosa.service.PaginatedList;

/**
 * Factory for account objects.
 */
@Component
public class AccountFactory extends BaseDomainObjectFactory implements DomainObjectFactory<Account> {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TokenFactory tokenFactory;

    /**
     * Get paginated list of accounts.
     *
     * @param limit      number of accounts
     * @param offset     offset into accounts
     * @param expansions fields to expand
     * @return paginated list of accounts
     */
    public PaginatedList<Account> getAccounts(Integer limit, Integer offset, String... expansions) {
        List<Account> accounts = accountRepository.list(limit, offset, expansions);
        return new PaginatedList<>(
                accounts,
                accountRepository.count(),
                limit, offset);
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
        Account account = accountRepository.get(id, expansions);
        injectDependencies(account);
        return account;
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
        Account account = accountRepository.getAccountByEmail(email, expansions);
        injectDependencies(account);
        return account;
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
    public void injectDependencies(Account account) {
        account.setAccountFactory(this);
        account.setAccountRepository(accountRepository);
        account.setTokenFactory(tokenFactory);
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
