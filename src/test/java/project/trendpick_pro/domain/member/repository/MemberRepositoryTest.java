//package project.trendpick_pro.domain.member.repository;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.transaction.annotation.Transactional;
//import project.trendpick_pro.IntegrationTestSupport;
//import project.trendpick_pro.domain.member.entity.Member;
//import project.trendpick_pro.domain.member.entity.MemberRole;
//import project.trendpick_pro.global.exception.BaseException;
//import project.trendpick_pro.global.exception.ErrorCode;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@Transactional
//class MemberRepositoryTest extends IntegrationTestSupport {
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @AfterEach
//    void tearDown() {
//        memberRepository.deleteAllInBatch();
//    }
//
//    @DisplayName("저장된 멤버 객체를 이름으로 찾을 수 있다.")
//    @Test
//    void findByUsername() throws Exception {
//        //given
//        Member member1 = createMember("member1", "TrendPick1@email.com", "Polo");
//        Member member2 = createMember("member2", "TrendPick2@email.com", "Polo");
//        memberRepository.saveAll(List.of(member1, member2));
//
//        //when
//        Member findMember = memberRepository.findByUsername(member2.getNickName())
//                .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
//
//        //then
//        assertThat(findMember).isNotNull();
//        assertThat(findMember.getNickName()).isEqualTo(member2.getNickName());
//    }
//
//    @DisplayName("저장된 멤버 객체를 이메일로 찾을 수 있다.")
//    @Test
//    void findByEmail() throws Exception {
//        //given
//        Member member1 = createMember("member1", "TrendPick1@email.com", "Polo");
//        Member member2 = createMember("member2", "TrendPick2@email.com", "Polo");
//        memberRepository.saveAll(List.of(member1, member2));
//
//        //when
//        Member findMember = memberRepository.findByEmail(member2.getEmail())
//                .orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
//
//        //then
//        assertThat(findMember).isNotNull();
//        assertThat(findMember.getEmail()).isEqualTo(member2.getEmail());
//    }
//
//    @DisplayName("저장된 멤버 객체를 브랜드이름으로 찾을 수 있다.")
//    @Test
//    void findByBrand() throws Exception {
//        Member member1 = createMember("member1", "TrendPick1@email.com", "Polo");
//        Member member2 = createMember("member2", "TrendPick2@email.com", "Nike");
//        memberRepository.saveAll(List.of(member1, member2));
//
//        //when
//        Member findMember = memberRepository.findByBrand(member2.getBrand()).orElseThrow(() -> new BaseException(ErrorCode.MEMBER_NOT_FOUND));
//
//        //then
//        assertThat(findMember).isNotNull();
//        assertThat(findMember.getBrand()).isEqualTo(member2.getBrand());
//    }
//
//    private Member createMember(String userName, String email, String brand) {
//        return Member.builder()
//                .email(email)
//                .nickName(userName)
//                .phoneNumber("010-1234-5678")
//                .role(MemberRole.MEMBER)
//                .brand(brand)
//                .build();
//    }
//}
