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

            for(int i = 0; i < 100; i++) {
                Member member = new Member();
                member.setUsername("member"+i);
                member.setAge(i);
                entityManager.persist(member);
            }

            entityManager.flush();
            entityManager.clear();

            // JPA에서는 그 복잡한 페이징을 setFirstResult, setMaxResult, 단 이 2개로 축약했다.
            // -> 오라클, MSSQL 같은 경우 페이징 작업이 좆 같다고 한다
            List<Member> resultList = entityManager.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(1) //　첫 번째에서
                    .setMaxResults(10) // 10개씩 페이징
                    .getResultList();
            System.out.println("resultList size = " + resultList.size());
            for (Member member1 : resultList) {
                System.out.println("member1 = " + member1);
            }

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {

            entityManager.close();
        }
        entityManagerFactory.close();

    }
}
