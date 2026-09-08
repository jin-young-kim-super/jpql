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

            //  JPQL에서 문자열 : 싱글 따옴표
            //  JPQL에서 불린형 : TRUE, FALSE, true, false
            //  JPAQL에서 ENUM : 패키지 경로를 전부 명시
            String jpql = "select m.username, 'HELLO', TRUE FROM Member m WHERE m.memberType = jpql.MemberType.ADMIN";
            List<Object[]> resultList = entityManager.createQuery(jpql).getResultList();

            /**
             * ENUM의 긴 패키지명이 짜는 날 경우 해결법
             * -> 바인딩으로 해결
             */
//            String jpql = "select m.username, 'HELLO', TRUE FROM Member m WHERE m.memberType = :memberType";
//            List<Object[]> resultList = entityManager.createQuery(jpql)
//                    .setParameter("memberType",MemberType.ADMIN)
//                    .getResultList();


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
