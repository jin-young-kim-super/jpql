import jakarta.persistence.*;
import jpql.Member;

import java.util.List;


public class JpaMain {

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hello");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin(); // 트랜잭션 시작

        try {
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            entityManager.persist(member);

            // 2번째 파라미터 : 응답 객체 타입 지정
            // -> 응답 객체 타입은 PROJECT에서 나중에 자세히 배움
            TypedQuery<Member> typedQuery = entityManager.createQuery("select m from Member m", Member.class); // TypeQuery : 응답 객체가 명확할 떄!
            Query query = entityManager.createQuery("select m.username, m.age from Member m"); // Query : 응답 객체가 명확하지 않을 떄!


            // getResultList() : List로 반환되며 결과가 여러 개 반환될 떄!
            List<Member> selectMFromMemberM = entityManager.createQuery("select m from Member m", Member.class).getResultList();

            // getSingleResult() : 결과가 1개만 반환될 떄!
            Member singleResult = entityManager.createQuery("select m from Member m where m.id = '10'", Member.class).getSingleResult();


            // 파라미터 바인딩 : 이름 기준
            TypedQuery<Member> parameterQuery = entityManager.createQuery("select m from Member m where m.username= :username", Member.class);
            parameterQuery.setParameter("username","member1");
            Member singRst = parameterQuery.getSingleResult();

            // 파라미터 바인딩 : 위치 기준(절대 사용하지  말것)
            TypedQuery<Member> parameterQuery2 = entityManager.createQuery("select m from Member m where m.username=?1", Member.class);
            parameterQuery2.setParameter(1,"kim"); // 만약에 where절에 바인딩 파라미터가 추가가 되거나, 하면 setParameter시 위치 1,2,3 등의 오기입이 생길 수 있어서 장애로 이어진다. 이름 기반은 그런 오류는 안 일어 난다.
            Member singleResult1 = parameterQuery2.getSingleResult();


            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        } finally {

            entityManager.close();
        }
        entityManagerFactory.close();

    }
}
