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

            // 기본 case문(변수가 when문 안에 있는 것)
            String jpql1 = "select " +
                                     "case when m.age <= 10 then '학생요금'"+
                                     "     when m.age >= 60 then '경로요금'"+
                                     "     else '일반요금'"+
                                     "end" +
                          " from Member as m";
            List<String> resultList1 = entityManager.createQuery(jpql1, String.class)
                    .getResultList();

            // 단순 case문(변수가 when문 밖에 있는 것, switch-case랑 비슷)
            String jpql2 = "select " +
                                    "case t.name  " +
                                    "     when '맨유' then '인센티브110%'"+
                                    "     when '토트넘' then '인센티브120%'"+
                                    "     else '인센티브100%'"+
                                    "end" +
                           " from Team as t";
            List<String> resultList2 = entityManager.createQuery(jpql2, String.class)
                    .getResultList();

            // COALESCE문 : 모든 레코드를 하나씪 조회해서 null인 경우 지정값 반환, null이 아닌 경우 해당 컬럼 반환
            String jpql3 = "select COALESCE(m.username,'이름 없는 회원') as username" +
                           " from Member as m";
            List<String> resultList3 = entityManager.createQuery(jpql3, String.class)
                    .getResultList();


            // NULLIF문 : 조건문 충족 시 NULL 반환
            String jpql4 = "select NULLIF(m.username,'이름 있는 회원') as username" +
                    " from Member as m";
            List<String> resultList4 = entityManager.createQuery(jpql4, String.class)
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
