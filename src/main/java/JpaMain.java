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
            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            entityManager.persist(member);

            entityManager.flush();
            entityManager.clear();

            // 엔티티 프로젝션 : 반드시 영속성 컨텍스트에 관리
            List<Member> findMembers = entityManager.createQuery("select m from Member m", Member.class)
                    .getResultList();

            // 엔티티 프로젝션 : 반드시 영속성 컨텍스트에 관리
            // -> 메모 참고!!!!
            List<Team> findTeams = entityManager.createQuery("select m.team from Member m", Team.class)
                    .getResultList();

            // 임베디드 프로젝션 : 영속성 컨텍스트에 관리x
            List<Address> findAddress = entityManager.createQuery("select o.address from Order o", Address.class)
                    .getResultList();

            // 스칼라 타입 프로젝션 : 영속성 컨텍스트에 관리x
            // -> 메모 참고.
            List resultList = entityManager.createQuery("select m.username, m.age from Member m")
                    .getResultList();

            Object o = resultList.get(0); // DB 결과의 첫 행
            Object[] result = (Object[]) o; // 업 캐스팅
            System.out.println("username=" + result[0]);
            System.out.println("age = " + result[1]);

            // selct절에 여러 개의 값을 projection하는 경우 젤 깔끔하는 방법
            List<MemberDto> resultList1 = entityManager.createQuery("select new jpql.MemberDto(m.username,m.age) from Member m", MemberDto.class)
                    .getResultList();
            for (MemberDto memberDto : resultList1) {
                System.out.println("memberDto = " + memberDto);
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
