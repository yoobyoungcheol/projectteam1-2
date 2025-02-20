package com.TFteamAI.team1AI.repository.fashion;

import com.TFteamAI.team1AI.entity.fashion.Fashion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FashionRepository extends JpaRepository<Fashion,Long> {

    // 의류 이름으로 총 개수 조회
    @Query("SELECT SUM(f.count) FROM Fashion f WHERE f.name = :name")
    Integer getTotalCountByName(@Param("name") String name);

    // trackId로 중복 체크
    Fashion findByTrackId(Integer trackId);
}
