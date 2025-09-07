package j2ee.ourteam.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import j2ee.ourteam.entities.User;

@Repository
public interface IUserRepository extends JpaRepository<User, String> {

}
