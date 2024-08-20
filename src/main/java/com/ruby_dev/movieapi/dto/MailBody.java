package com.ruby_dev.movieapi.dto;

import lombok.Builder;

@Builder
public record MailBody(String to, String subject, String text) {
}
