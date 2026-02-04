package prs.fmtareco.adventure.loader.mapper;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import prs.fmtareco.adventure.loader.json.SectionJson;
import prs.fmtareco.adventure.model.Section;

@Component
@RequiredArgsConstructor
public class SectionMapper {

    private final OptionMapper optionMapper;

    public Section fromJson(SectionJson json) {
        Section section = new Section();
        section.setSectionNumber(json.id());  // JSON id → sectionNumber
        section.setText(json.text());
        section.setType(json.type());
        if (json.options() != null) {
            json.options().forEach(
                optJson ->
                        section.addOption(optionMapper.fromJson(optJson)));
        }

        return section;
    }
}