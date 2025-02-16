package com.TFteamAI.team1AI.repository.fashion;

import com.TFteamAI.team1AI.entity.fashion.Fashion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FashionRepository extends JpaRepository<Fashion,Long> {


}
