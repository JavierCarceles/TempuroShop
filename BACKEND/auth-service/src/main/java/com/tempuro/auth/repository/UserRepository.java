package com.tempuro.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tempuro.auth.model.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /*
     * El optional Devuelve un User si existe o un objeto vacío si no
     */
    Optional<User> findByEmail(String email);

}
