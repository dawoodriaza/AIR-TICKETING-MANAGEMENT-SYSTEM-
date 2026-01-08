package com.ga.airticketmanagement.event;

import com.ga.airticketmanagement.model.User;

public record EmailVerificationRequestedEvent (User user, String token) {}
