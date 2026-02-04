package prs.fmtareco.adventure.loader.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import prs.fmtareco.adventure.loader.json.ConsequenceJson;
import prs.fmtareco.adventure.loader.json.OptionJson;
import prs.fmtareco.adventure.model.Consequence;
import prs.fmtareco.adventure.model.Option;

@Component
@RequiredArgsConstructor
public class OptionMapper {

    public Option fromJson(OptionJson json) {
        Option option = new Option();
        option.setDescription(json.description());
        option.setGotoSectionNumber(json.gotoId());

        if (json.consequence() != null) {
            ConsequenceJson cJson = json.consequence();
            Consequence consequence = new Consequence();
            consequence.setType(cJson.type());
            consequence.setValue(cJson.value());
            consequence.setText(cJson.text());
            consequence.setOption(option);
            option.setConsequence(consequence);
        }
        return option;
    }
}