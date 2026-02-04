package prs.fmtareco.adventure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prs.fmtareco.adventure.model.Option;

public interface OptionRepository extends JpaRepository<Option, Long> {
}
