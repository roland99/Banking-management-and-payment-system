package com.montran.internship.roland_gonczel.service;

import com.montran.internship.roland_gonczel.dto.AccountDto;
import com.montran.internship.roland_gonczel.entity.Account;
import com.montran.internship.roland_gonczel.entity.AccountHistory;
import com.montran.internship.roland_gonczel.entity.User;
import com.montran.internship.roland_gonczel.entity.UserHistory;
import com.montran.internship.roland_gonczel.mapper.AccountHistoryMapper;
import com.montran.internship.roland_gonczel.mapper.AccountMapper;
import com.montran.internship.roland_gonczel.mapper.UserHistoryMapper;
import com.montran.internship.roland_gonczel.repository.AccountHistoryRepository;
import com.montran.internship.roland_gonczel.repository.AccountRepository;
import com.montran.internship.roland_gonczel.status.AccountStatus;
import com.montran.internship.roland_gonczel.status.Operation;
import com.montran.internship.roland_gonczel.status.PaymentType;
import com.montran.internship.roland_gonczel.status.Status;
import com.montran.internship.roland_gonczel.utility.AccountFieldValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService implements CrudOperationService<AccountDto, AccountHistory> {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountHistoryRepository accountHistoryRepository;

    @Autowired
    UserService userService;

    @Autowired
    BalanceService balanceService;

    public AccountDto findById(String id){
        Optional<Account> a = accountRepository.findById(id);
        Account account = Optional.ofNullable(a).map(o->o.get()).orElse(null);
        if(account != null){
            AccountDto accountDto = new AccountDto();
            accountDto = AccountMapper.toAccountDto(account,accountDto);
            log.info("Account with number " + account.getNumber() + " is returned");
            return accountDto;
        }else{
            log.warn("No account with that id");
            return new AccountDto();
        }
    }

    public AccountDto findByNumber(String number){
        AccountDto accountDto = AccountMapper.toAccountDto(accountRepository.findByNumber(number), new AccountDto());
        return accountDto;
    }

    private void updateAccountHistory(Account account, Operation operation,AccountDto accountDto){
        accountDto.setId(account.getId());

        AccountHistory accountHistory = new AccountHistory();
        AccountHistoryMapper.updateAccountFromDto(accountDto,accountHistory);
        accountHistory.setInitiatorUsername(userService.getSessionUserUsername());
        accountHistory.setOperation(operation);
        accountHistory.setWaitingResponse(true);
        accountHistory.setDate(new Timestamp(System.currentTimeMillis()));
        accountHistory.setAccount(account);

        account.addAccountHistory(accountHistory);
        account.setStatus(Status.APROVE);

        accountRepository.saveAndFlush(account);
        log.info("Account history updated");
    }

    private void saveWithHistory(Account account, Operation operation,boolean waitResponse){
        AccountHistory accountHistory = AccountHistoryMapper.toAccountHistory(account);
        accountHistory.setInitiatorUsername(userService.getSessionUserUsername());
        accountHistory.setOperation(operation);
        accountHistory.setWaitingResponse(waitResponse);
        accountHistory.setDate(new Timestamp(System.currentTimeMillis()));
        accountHistory.setAccount(account);

        account.addAccountHistory(accountHistory);
        accountRepository.saveAndFlush(account);
    }

    private void invalidateOtherRequests(Account account){

        for(AccountHistory ah: account.getAccountHistory()){
            ah.setWaitingResponse(false);
            accountHistoryRepository.saveAndFlush(ah);
        }
        accountRepository.saveAndFlush(account);
    }

    @Override
    public List<AccountHistory> findAllApproveRequests(){
        List<AccountHistory> list = new ArrayList<>();
        for(AccountHistory ah: accountHistoryRepository.findAllByWaitingResponseTrue()){
            list.add(ah);
        }
        return list;
    }

    @Override
    public List<AccountDto> viewAllActive() {
        List<AccountDto> list = new ArrayList<>();
        for(Account a: accountRepository.findAllByStatusOrStatus(Status.ACTIVE,Status.APROVE)){
            list.add(AccountMapper.toAccountDto(a,new AccountDto()));
        }
        return list;
    }

    @Override
    public void addNewEntry(AccountDto accountDto) {
        if(accountDto !=null){
            if(AccountFieldValidator.verifyDto(accountDto)){
                Account account = new Account();
                account = AccountMapper.toAccount(accountDto,account);
                account.setId(UUID.randomUUID().toString());
                account.setStatus(Status.CREATE);
                account.setAccountStatus(AccountStatus.OPEN);
                account.setNumber(account.getNumber().replaceAll("\\s+",""));
                saveWithHistory(account,Operation.ADD,true);
                log.info("Succesfully crreated pendig account creation");
            }else{
                log.warn("Invalid fields for the new account");
            }
        }else{
            log.warn("AccountDto is null");
        }

    }

    @Override
    public void modifyEntry(AccountDto accountDto, String id) {
        Optional<Account> a = accountRepository.findById(id);
        Account account = Optional.ofNullable(a).map(o->o.get()).orElse(null);
        if(account != null){
            if(AccountFieldValidator.verifyDto(accountDto)){
                accountDto.setStatus(Status.APROVE);
                updateAccountHistory(account,Operation.MODIFY,accountDto);
                log.info("Successfully created pending modification for account");
            }else{
                log.warn("Invalid fields for the modification");
            }
        }else{
            log.warn("AccountDto is null");
        }
    }

    @Override
    public void deleteEntry(String id) {
        Optional<Account> a = accountRepository.findById(id);
        Account account = Optional.ofNullable(a).map(o->o.get()).orElse(null);
        if(account != null){
            account.setStatus(Status.APROVE);
            saveWithHistory(account,Operation.DELETE,true);
            log.info("Successfully created pending deletion for account: " + account.getNumber());
        }

    }

    @Override
    public void approveEntry(String id) {
        Optional<AccountHistory> ah = accountHistoryRepository.findById(id);
        AccountHistory accountHistory = Optional.ofNullable(ah).map(o->o.get()).orElse(null);
        if(accountHistory != null && !accountHistory.getInitiatorUsername().equals(userService.getSessionUserUsername())){
            switch (accountHistory.getOperation()){
                case ADD:
                    addFromHistory(accountHistory);
                    break;
                case DELETE:
                    deleteFromHistory(accountHistory);
                    break;
                case MODIFY:
                    modifyFromHistory(accountHistory);
                    break;

            }
        }
    }


    @Override
    public void rejectEntry(String id) {
        Optional<AccountHistory> ah = accountHistoryRepository.findById(id);
        AccountHistory accountHistory = Optional.ofNullable(ah).map(o->o.get()).orElse(null);
        if(accountHistory != null && !accountHistory.getInitiatorUsername().equals(userService.getSessionUserUsername())){
            Optional<Account> a = accountRepository.findById(accountHistory.getAccountId());
            Account account = Optional.ofNullable(a).map(o->o.get()).orElse(null);
            if(account != null){
                accountHistory.setWaitingResponse(false);
                account.setStatus(Status.ACTIVE);
                saveWithHistory(account,Operation.REJECTED,false);
                //In case of reject on account add, deleted the temporary created account from db
                if(accountHistory.getOperation().equals(Operation.ADD)){
                    accountRepository.delete(account);
                }
            }
            log.info("Request rejected for account: " + account.getNumber());
        }
    }

    @Override
    public void addFromHistory(AccountHistory accountHistory) {
        Optional<Account> a = accountRepository.findById(accountHistory.getAccountId());
        Account account = Optional.ofNullable(a).map(o->o.get()).orElse(null);
        if(account != null) {
            accountHistory.setWaitingResponse(false);
            account.setStatus(Status.ACTIVE);
            account.addAccountHistory(accountHistory);
            account.setAccountBalance(balanceService.createBalanceForAccount(account));
            saveWithHistory(account,Operation.APROVED, false);
            log.info("New account : " + account.getNumber() + " created");
        }else {
            log.warn("No such account to add in the database");
        }
    }

    @Override
    public void modifyFromHistory(AccountHistory accountHistory) {
        Optional<Account> a = accountRepository.findById(accountHistory.getAccountId());
        Account account = Optional.ofNullable(a).map(o->o.get()).orElse(null);
        if(account != null) {
            account = AccountHistoryMapper.toAccount(accountHistory);
            account.setStatus(Status.ACTIVE);
            accountHistory.setWaitingResponse(false);
            saveWithHistory(account,Operation.APROVED,false);
            log.info("Account: "+ account.getNumber() + " modified successfully");

        }else {
            log.warn("No such account to modify in the database");
        }
    }

    @Override
    public void deleteFromHistory(AccountHistory accountHistory) {
        Optional<Account> a = accountRepository.findById(accountHistory.getAccountId());
        Account account = Optional.ofNullable(a).map(o->o.get()).orElse(null);
        if(account != null) {
            account.setStatus(Status.DELETED);
            accountHistory.setWaitingResponse(false);
            invalidateOtherRequests(account);
            saveWithHistory(account,Operation.APROVED,false);
            log.info("Account: " + account.getNumber() + " deleted");
        }else{
            log.warn("No such account tot delete");
        }
    }

    /* Payment logic */
    @Transactional
    public void initializePayment(String debit, String credit, long amount, PaymentType paymentType){
        Account debitAccount = accountRepository.findByNumber(debit);
        Account creditAccount = null;
        if(paymentType.equals(PaymentType.INTERNAL)) {
            creditAccount = accountRepository.findByNumber(credit);
        }
        if(debitAccount != null ){
            debitAccount.getAccountBalance().setPendingAmountDebit(debitAccount.getAccountBalance().getPendingAmountDebit() + amount);
            debitAccount.getAccountBalance().setPendingCountDebit(debitAccount.getAccountBalance().getPendingCountDebit() + 1);
            if(paymentType.equals(PaymentType.INTERNAL) && creditAccount != null) {
                creditAccount.getAccountBalance().setPendingAmountCredit(creditAccount.getAccountBalance().getPendingAmountCredit() + amount);
                creditAccount.getAccountBalance().setPendingCountCredit(creditAccount.getAccountBalance().getPendingCountCredit() + 1);
                balanceService.saveWithHistory(creditAccount.getAccountBalance(), Operation.MODIFY, false);
                accountRepository.saveAndFlush(creditAccount);

            }
            balanceService.saveWithHistory(debitAccount.getAccountBalance(),Operation.MODIFY,false);


            accountRepository.saveAndFlush(debitAccount);


        }
    }


    @Transactional
    public void finalizePayment(String debit, String credit, long amount, PaymentType paymentType){
        Account debitAccount = accountRepository.findByNumber(debit);
        Account creditAccount = null;
        if(paymentType.equals(PaymentType.INTERNAL)) {
            creditAccount = accountRepository.findByNumber(credit);
        }
        if(debitAccount != null){
            //reverse pending debit
            debitAccount.getAccountBalance().setPendingAmountDebit(debitAccount.getAccountBalance().getPendingAmountDebit() - amount);
            debitAccount.getAccountBalance().setPendingCountDebit(debitAccount.getAccountBalance().getPendingCountDebit() - 1);
            //update debit account
            debitAccount.getAccountBalance().setAvailableAmountDebit(debitAccount.getAccountBalance().getAvailableAmountDebit() + amount);
            debitAccount.getAccountBalance().setAvailableCountDebit(debitAccount.getAccountBalance().getAvailableCountDebit() + 1);

            if(paymentType.equals(PaymentType.INTERNAL) && creditAccount != null) {
                //reverse pending credit
                creditAccount.getAccountBalance().setPendingAmountCredit(creditAccount.getAccountBalance().getPendingAmountCredit() - amount);
                creditAccount.getAccountBalance().setPendingCountCredit(creditAccount.getAccountBalance().getPendingCountCredit() - 1);
                //update credit account
                creditAccount.getAccountBalance().setAvailableAmountCredit(creditAccount.getAccountBalance().getAvailableAmountCredit() + amount);
                creditAccount.getAccountBalance().setAvailableCountCredit(creditAccount.getAccountBalance().getAvailableCountCredit() + 1);
                balanceService.saveWithHistory(creditAccount.getAccountBalance(), Operation.APROVED, false);
                accountRepository.saveAndFlush(creditAccount);
            }

            balanceService.saveWithHistory(debitAccount.getAccountBalance(), Operation.APROVED, false);


            accountRepository.saveAndFlush(debitAccount);
        }
    }

    @Transactional
    public void cancelPayment(String debit, String credit, long amount, PaymentType paymentType){
        Account debitAccount = accountRepository.findByNumber(debit);
        Account creditAccount = null;
        if(paymentType.equals(PaymentType.INTERNAL)) {
            creditAccount = accountRepository.findByNumber(credit);
        }
        if(debitAccount != null ){
            debitAccount.getAccountBalance().setPendingAmountDebit(debitAccount.getAccountBalance().getPendingAmountDebit() - amount);
            debitAccount.getAccountBalance().setPendingCountDebit(debitAccount.getAccountBalance().getPendingCountDebit() - 1);
            if(paymentType.equals(PaymentType.INTERNAL) && creditAccount != null) {
                creditAccount.getAccountBalance().setPendingAmountCredit(creditAccount.getAccountBalance().getPendingAmountCredit() - amount);
                creditAccount.getAccountBalance().setPendingCountCredit(creditAccount.getAccountBalance().getPendingCountCredit() - 1);
                balanceService.saveWithHistory(creditAccount.getAccountBalance(), Operation.REJECTED, false);
                accountRepository.saveAndFlush(creditAccount);
            }
            balanceService.saveWithHistory(debitAccount.getAccountBalance(),Operation.REJECTED,false);


            accountRepository.saveAndFlush(debitAccount);


        }
    }



    public boolean validateAccountsForPayment(String debit, String credit, StringBuilder message, PaymentType paymentType){
        Account accountDebit = accountRepository.findByNumber(debit);
        Account accountCredit = accountRepository.findByNumber(credit);

        if(accountDebit == null || accountDebit.getAccountStatus().equals(AccountStatus.BLOCK_DEBIT) || accountDebit.getAccountStatus().equals(AccountStatus.BLOCKED) || accountDebit.getAccountStatus().equals(AccountStatus.CLOSED)){
            message.delete(0, message.length());
            message.append("Debitorul nu poate trimite plati");
            return false;
        }
        if(paymentType.equals(PaymentType.INTERNAL)) {
            if (accountCredit == null || accountCredit.getAccountStatus().equals(AccountStatus.BLOCK_CREDIT) || accountCredit.getAccountStatus().equals(AccountStatus.BLOCKED) || accountCredit.getAccountStatus().equals(AccountStatus.CLOSED)) {
                message.delete(0, message.length());
                message.append("Creditorul nu poate primi plati");
                return false;
            }
        }
        return true;

    }
}
