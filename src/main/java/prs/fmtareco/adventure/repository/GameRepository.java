package prs.fmtareco.adventure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prs.fmtareco.adventure.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
}
