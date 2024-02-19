package com.kbtg.bootcamp.posttest.authenticate;

import com.kbtg.bootcamp.posttest.security.CustomUserDetail;
import com.kbtg.bootcamp.posttest.user_account.UserAccount;
import com.kbtg.bootcamp.posttest.user_account.UserAccountRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    private final UserAccountRepository userAccountRepository;

    public CustomUserDetailService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public CustomUserDetail loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByUsername(username);

        if (userAccount == null) {
            throw new UsernameNotFoundException("User not exists by Username");
        }

        CustomUserDetail userDetail = new CustomUserDetail(userAccount.getUsername(), userAccount.getPassword());
        userDetail.setRoles(userAccount.getRoles());
        return userDetail;
    }
}
