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

            Team teamA = new Team();
            teamA.setName("teamA");
            entityManager.persist(teamA);

            Team teamB = new Team();
            teamB.setName("teamB");
            entityManager.persist(teamB);


            Member member1 = new Member();
            member1.setUsername("member1");
            member1.setAge(10);
            member1.setMemberType(MemberType.ADMIN);
            member1.changeTeam(teamA);
            entityManager.persist(member1);


            Member member2 = new Member();
            member1.setUsername("member2");
            member1.setAge(20);
            member1.setMemberType(MemberType.ADMIN);
            member1.changeTeam(teamA);
            entityManager.persist(member2);

            Member member3 = new Member();
            member3.setUsername("member3");
            member3.setAge(30);
            member3.setMemberType(MemberType.ADMIN);
            member3.changeTeam(teamB);
            entityManager.persist(member3);

            entityManager.flush();
            entityManager.clear();
            /**
             * 단일값 연관 관계의 fetch join
             */
            // fetch join 미사용
            String jpql1 = "select m From Member as m";
            List<Member> resultList1 = entityManager.createQuery(jpql1, Member.class)
                    .getResultList();

            // N + 1문제 발생
            // -> 경로 표현식으로 Team 사용!
            for (Member member : resultList1) {
                System.out.println("member = " + member.getUsername() + member.getTeam().getName());
            }

            // fetch join 사용
            String jpql2 = "select m From Member as m join fetch m.team t";
            List<Member> resultList2 = entityManager.createQuery(jpql2, Member.class)
                    .getResultList();

            // N + 1문제 발생x
            // -> 경로 표현식으로 Team 사용!
            for (Member member : resultList1) {
                System.out.println("member = " + member.getUsername() + member.getTeam().getName());
            }

            /**
             * 컬렉션 값 연관 관계의 fetch join(@OnetoMany 혹은 컬렉션 값 타입)
             */
            String jpql4 = "select t from Team as t join fetch t.memebers";
            List<Team> resultList4 = entityManager.createQuery(jpql4, Team.class)
                    .getResultList();

            /**
             * 중복된 Team 객체가 출력(강의자료에 중복을 그림으로 표시해 놓은 부분 참조하조)
             * -> @..toOne 관계인 Team 엔티티를 기준으로 join이 일어나므로, Member 테이블에 TEAM_ID(FK)가 2개 있으면
             * 같은 Team 엔티티가 레코드에 중복될 수가 있다. 영속성 컨텍스트에는 TeamA 엔티티 하나만 저장되고, getResultList()에 의해 컬렉션이 반환될 때, 영속성 컨텍스트의 TeamA의 참조값 2개가 들어간 컬렉션으로 반환
               * ex) TeamA : 2행(중복)
             *       TeabB : 1행
             */
            for (Team team : resultList4) {
                System.out.println("team = " + team.getName() + team.getMembers().size());
            }

            /**
             * DISTINCT : 1. SQL의 결과 모든 컬럼이 같은 행을 삭제, 2. JPA의 고유 기능 : 같은 식별자를 가진 엔티티 제거!!!
             * -> DISTINCT를 사용하여, 컬렉션 값 조회 시 발생하는 중복 객체 삭제
             */
            String jpql5 = "select DISTINCT t from Team as t join fetch t.memebers";
            List<Team> resultList5 = entityManager.createQuery(jpql5, Team.class)
                    .getResultList();


            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {

            entityManager.close();
        }
        entityManagerFactory.close();

    }
}
