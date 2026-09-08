import jakarta.persistence.*;
import jpql.Address;
import jpql.Member;
import jpql.MemberDto;
import jpql.Team;

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
            member.changeTeam(team);
            entityManager.persist(member);

            // INNER JOIN(INNER 생략 가능)
            String jpql1 = "select m from Member m inner join m.team t";
            List<Member> resultList1 = entityManager.createQuery(jpql1, Member.class)
                    .getResultList();

            // LEFT OUTER JOIN(OUTER 생략 가능)
            String jpql2 = "select m from Member m left outer join m.team t";
            List<Member> resultList2 = entityManager.createQuery(jpql2, Member.class)
                    .getResultList();

            // THETA JOIN
            String jpql3 = "select m from Member m , m.team t";
            List<Member> resultList3 = entityManager.createQuery(jpql3, Member.class)
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
