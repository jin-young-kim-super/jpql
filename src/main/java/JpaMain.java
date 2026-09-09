import jakarta.persistence.*;
import jpql.*;

import java.util.List;


public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin(); // 트랜잭션 시작

        try {

            Team team = new Team();
            team.setName("teamA");
            entityManager.persist(team);

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            member.setMemberType(MemberType.ADMIN);
            member.changeTeam(team);
            entityManager.persist(member);

            // JPA 내장 함수 사용 예시
            String jpql = "select concat('a','b') From Member as m";
            List<String> resultList = entityManager.createQuery(jpql, String.class)
                    .getResultList();

            entityManager.flush();
            entityManager.clear();

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {

            entityManager.close();
        }
        entityManagerFactory.close();

    }
}
