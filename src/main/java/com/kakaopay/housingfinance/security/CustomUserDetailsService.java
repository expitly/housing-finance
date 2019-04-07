package com.kakaopay.housingfinance.security;

import com.kakaopay.housingfinance.domain.auth.*;
import com.kakaopay.housingfinance.dto.auth.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("user name not found"));

        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities().stream()
                                .map(authority -> new SimpleGrantedAuthority(authority.getName().name()))
                                .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public CustomUserDetails signUp(SignUpRequest signUpRequest){
        checkDuplicateByUsername(signUpRequest.getUsername());

        User user = userRepository.save(User.builder()
                .username(signUpRequest.getUsername())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .authorities(Arrays.asList(getAuthority(AuthorityName.ROLE_API)))
                .build());

        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getName().name()))
                        .collect(Collectors.toList()))
                .build();
    }

    private void checkDuplicateByUsername(String username) {
        if(userRepository.findByUsername(username).isPresent()){
            throw new IllegalArgumentException("user name is duplicated");
        }
    }

    private Authority getAuthority(AuthorityName name) {
        return authorityRepository.findByName(name).orElseThrow(() -> new IllegalArgumentException("authority not found"));
    }
}
