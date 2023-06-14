package com.auth.server.authserver.component;

import com.auth.server.authserver.entity.Account;
import com.auth.server.authserver.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

//@Component
public class AccountUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("用户名密码不匹配");
        }
        return User
//                .withUsername(username).password("{noop}" + account.getPassword())
                .withUsername(username).password(account.getPassword())
                .roles("read")
                .build();
    }
}
