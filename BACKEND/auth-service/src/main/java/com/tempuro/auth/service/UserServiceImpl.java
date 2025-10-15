package com.tempuro.auth.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.tempuro.auth.model.User;
import com.tempuro.auth.repository.UserRepository;
import com.tempuro.auth.exception.UserNotFoundException;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;

@Service
public class UserServiceImpl implements UserServiceInterface, UserDetailsService {

    private final UserRepository userRepository;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("user-service");
    private static final String USER_NOT_FOUND = "Usuario no encontrado";
    private static final String SPAN_ATTR_USER_EMAIL = "user.email";

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Span span = tracer.spanBuilder("load-user-by-username").startSpan();
        span.setAttribute(SPAN_ATTR_USER_EMAIL, email);
        try (Scope scope = span.makeCurrent()) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        span.setStatus(StatusCode.ERROR, USER_NOT_FOUND);
                        return new UserNotFoundException(USER_NOT_FOUND);
                    });
        } finally {
            span.end();
        }
    }

    @Override
    public User findByEmail(String email) {
        Span span = tracer.spanBuilder("find-user-by-email").startSpan();
        span.setAttribute(SPAN_ATTR_USER_EMAIL, email);
        try (Scope scope = span.makeCurrent()) {
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        span.setStatus(StatusCode.ERROR, USER_NOT_FOUND);
                        return new UserNotFoundException(USER_NOT_FOUND);
                    });
        } finally {
            span.end();
        }
    }

    public boolean existsByEmail(String email) {
        Span span = tracer.spanBuilder("check-user-existence").startSpan();
        span.setAttribute(SPAN_ATTR_USER_EMAIL, email);
        try (Scope scope = span.makeCurrent()) {
            boolean exists = userRepository.findByEmail(email).isPresent();
            span.setAttribute("user.exists", exists);
            return exists;
        } finally {
            span.end();
        }
    }

    public User saveUser(User user) {
        Span span = tracer.spanBuilder("save-user").startSpan();
        span.setAttribute(SPAN_ATTR_USER_EMAIL, user.getEmail());
        try (Scope scope = span.makeCurrent()) {
            return userRepository.save(user);
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, "Error guardando usuario: " + e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }

    public User saveAndFlush(User user) {
        Span span = tracer.spanBuilder("save-and-flush-user").startSpan();
        span.setAttribute(SPAN_ATTR_USER_EMAIL, user.getEmail());
        try (Scope scope = span.makeCurrent()) {
            return userRepository.saveAndFlush(user);
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR, "Error guardando usuario (flush): " + e.getMessage());
            throw e;
        } finally {
            span.end();
        }
    }
}
