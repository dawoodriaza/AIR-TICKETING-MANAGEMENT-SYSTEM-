package com.ga.airticketmanagement.listener;

import com.ga.airticketmanagement.event.EmailVerificationRequestedEvent;
import com.ga.airticketmanagement.service.AccountEmailService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EmailEventListener {

    private final AccountEmailService accountEmailService;

    public EmailEventListener(AccountEmailService accountEmailService) {
        this.accountEmailService = accountEmailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleEmailVerification(EmailVerificationRequestedEvent event){
        accountEmailService.sendVerificationEmail(event.user(), event.token());
    }
}
