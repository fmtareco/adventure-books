package prs.fmtareco.adventure.repository;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import prs.fmtareco.adventure.model.Game;

public interface GameRepository extends JpaRepository<@NonNull Game, @NonNull Long> {

    @NonNull Page<@NonNull Game> findAll(@NonNull Pageable pageable);
    @NonNull Page<@NonNull Game> findAllByStatus(Game.Status status, @NonNull Pageable pageable);

}
