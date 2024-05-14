package com.example.shop.service;


import com.example.shop.dto.request.*;
import com.example.shop.dto.response.AuthenticationResponse;
import com.example.shop.dto.response.IntrospectResponse;
import com.example.shop.entily.InvalidatedToken;
import com.example.shop.entily.User;
import com.example.shop.repository.InvalidatedTokeRepository;
import com.example.shop.repository.UserRepository;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorCode;

import java.text.ParseException;
import java.util.StringJoiner;
import java.util.UUID;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import com.nimbusds.jwt.*;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import java.util.Date;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.util.CollectionUtils;

@Service
public class AuthenticationService {


    private UserRepository userRepository;

    private InvalidatedTokeRepository invalidatedTokeRepository;

    @Value("${jwt.signerKey}")
    private String SIGNER_KEY;

    @Autowired
    public AuthenticationService(UserRepository userRepository, InvalidatedTokeRepository invalidatedTokeRepository) {
        this.userRepository = userRepository;
        this.invalidatedTokeRepository = invalidatedTokeRepository;
    }

    public ApiResponse<AuthenticationResponse> login(AuthenticaticationRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean isPasswordCorrect = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (isPasswordCorrect) {
            // Tạo token
            String token = generateToken(user);

            // Authentication successful
            AuthenticationResponse response = new AuthenticationResponse(token, true);
            return new ApiResponse<>(200, "Authentication successful", response);
        } else {
            // Authentication failed
            throw new AppException(ErrorCode.AUTH_EX);
        }
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("devteria.com")
                .issueTime(new Date())
                .expirationTime(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user)) // Đây là nơi bạn có thể đặt phạm vi của token
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(generateSignerKey()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Cannot create token", e);
        }
    }

    private byte[] generateSignerKey() {
        return SIGNER_KEY.getBytes();
    }



    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token);
        } catch (AppException e) {
            isValid = false;
        }

        return new IntrospectResponse.Builder()
                .active(isValid)
                .build();
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName()); // Assuming name is the property that uniquely identifies the role
                if(!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> stringJoiner.add(permission.getName()));
            });
        return stringJoiner.toString();
    }


    public void logout(LogoutRequest request) throws JOSEException, ParseException {
        var signToken = verifyToken(request.getToken());

        String jit = signToken.getJWTClaimsSet().getJWTID();

        Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryDate(expiryTime).build();

        invalidatedTokeRepository.save(invalidatedToken);
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(generateSignerKey());

        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date())))
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        if (invalidatedTokeRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        return signedJWT;
    }

    public AuthenticationResponse refreshToken(RefreshRequest request)
            throws JOSEException, ParseException {
        var signedJWT = verifyToken(request.getToken());

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();


        InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryDate(expiryTime).build();

        invalidatedTokeRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));
        var token = generateToken(user);
        // Authentication successful
        AuthenticationResponse response = new AuthenticationResponse(token, true);
        return new ApiResponse<>(200, "Authentication successful 2", response).getResult();
    }

}

