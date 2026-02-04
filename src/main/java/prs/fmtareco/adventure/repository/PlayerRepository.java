package prs.fmtareco.adventure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prs.fmtareco.adventure.model.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
