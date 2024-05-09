package com.example.shop.repository;

import com.example.shop.entily.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface InvalidatedTokeRepository extends JpaRepository<InvalidatedToken, String> {
}
