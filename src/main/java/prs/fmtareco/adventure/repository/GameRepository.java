package prs.fmtareco.adventure.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import prs.fmtareco.adventure.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {

    Page<Game> findAll(Pageable pageable);
    Page<Game> findAllByStatus(Game.Status status, Pageable pageable);

}
