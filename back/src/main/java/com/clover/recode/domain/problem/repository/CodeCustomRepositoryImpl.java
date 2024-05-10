package com.clover.recode.domain.problem.repository;
import com.clover.recode.domain.problem.entity.*;
import com.clover.recode.domain.recode.entity.QRecode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.util.List;


@RequiredArgsConstructor
public class CodeCustomRepositoryImpl implements CodeCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Code> findByReviewStatusFalseAndReviewTimeBefore() {

        LocalDate today= LocalDate.now();
        QRecode recode= QRecode.recode;
        QCode code= QCode.code;

        return jpaQueryFactory.selectFrom(code)
                .where(code.deleted.eq(false),
                    code.reviewStatus.eq(true),
                        recode.reviewTime.before(today.atStartOfDay().plusDays(1)))
                .fetch();

    }

    @Override
    public Page<Problem> findProblemsByUserId(Long userId, Pageable pageable, Integer start, Integer end, List<String> tags, String keyword) {
        QCode qCode = QCode.code;
        QProblem qProblem = QProblem.problem;
        QTag qTag = QTag.tag;

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(qCode.user.id.eq(userId));

        // 레벨 - start, end 조건에 따라 추가 여부 선택
        if (start != null || end != null) {
            // start 또는 end 중 하나라도 있는 경우
            if (start == null) {
                start = 1; // start가 null이면 최소값 1으로 설정
            }
            if (end == null) {
                end = 30; // end가 null이면 최대값을 30으로 설정
            }
            whereClause.and(qProblem.level.between(start, end));
        }

        // 태그 - tags가 null이 아니고 비어 있지 않을 때만 조건 적용
        if (tags != null && !tags.isEmpty()) {
            whereClause.and(qProblem.tags.any().name.in(tags));//Problem과 연관된 Tag의 name 중 일치하는 것 검색
        }

        // 키워드 - keyword가 null이 아니고 공백이 아닐 때만 조건 적용
        if (keyword != null && !keyword.isBlank()) {
            whereClause.and(qProblem.title.containsIgnoreCase(keyword));//Problem의 title 필드에 keyword를 대소문자 구분 없이 포함하는 Problem을 검색
        }

        // 쿼리 실행 및 결과 반환 (페이징 처리)
        List<Problem> problems = jpaQueryFactory
                .select(qProblem)
                .from(qCode)
                .join(qCode.problem, qProblem)
                .leftJoin(qProblem.tags, qTag) // 태그 조인
                .where(whereClause)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 조회 (표시할 데이터의 총 페이지 수를 계산하기 위해 전체 데이터의 수를 알아야 함)
        long count = jpaQueryFactory
                .select(qProblem.count())
                .from(qCode)
                .join(qCode.problem, qProblem)
                .leftJoin(qProblem.tags, qTag) // 태그 조인
                .where(whereClause)
                .fetchOne();
        return new PageImpl<>(problems, pageable, count);
    }
}
