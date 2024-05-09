package com.example.shop.repository;


import com.example.shop.entily.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    @EntityGraph(attributePaths = {"permissions"})
    List<Role> findAll();
}
