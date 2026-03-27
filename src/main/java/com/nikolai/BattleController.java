package com.nikolai;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

import java.util.List;

public class BattleController {

    // Party slots (sprites)
    @FXML private ImageView playerSlot1Icon;
    @FXML private ImageView playerSlot2Icon;
    @FXML private ImageView playerSlot3Icon;
    @FXML private ImageView playerSlot4Icon;
    @FXML private ImageView playerSlot5Icon;
    @FXML private ImageView playerSlot6Icon;
    @FXML private ImageView oppSlot1Icon;
    @FXML private ImageView oppSlot2Icon;
    @FXML private ImageView oppSlot3Icon;
    @FXML private ImageView oppSlot4Icon;
    @FXML private ImageView oppSlot5Icon;
    @FXML private ImageView oppSlot6Icon;

    @FXML private Button playerSlot1;
    @FXML private Button playerSlot2;
    @FXML private Button playerSlot3;
    @FXML private Button playerSlot4;
    @FXML private Button playerSlot5;
    @FXML private Button playerSlot6;
    @FXML private Button oppSlot1;
    @FXML private Button oppSlot2;
    @FXML private Button oppSlot3;
    @FXML private Button oppSlot4;
    @FXML private Button oppSlot5;
    @FXML private Button oppSlot6;

    @FXML
    private void initialize() {
        // No timer/terastallize
    }

    public void bindPlayerTeam(List<Pokemon> team) {
        if (team == null) return;
        setSlotSprite(playerSlot1Icon, team, 0);
        setSlotSprite(playerSlot2Icon, team, 1);
        setSlotSprite(playerSlot3Icon, team, 2);
        setSlotSprite(playerSlot4Icon, team, 3);
        setSlotSprite(playerSlot5Icon, team, 4);
        setSlotSprite(playerSlot6Icon, team, 5);
    }

    public void bindOpponentTeam(List<Pokemon> team) {
        if (team == null) return;
        setSlotSprite(oppSlot1Icon, team, 0);
        setSlotSprite(oppSlot2Icon, team, 1);
        setSlotSprite(oppSlot3Icon, team, 2);
        setSlotSprite(oppSlot4Icon, team, 3);
        setSlotSprite(oppSlot5Icon, team, 4);
        setSlotSprite(oppSlot6Icon, team, 5);
    }

    private void setSlotSprite(ImageView slotImage, List<Pokemon> team, int index) {
        if (slotImage == null || team == null || index < 0 || index >= team.size()) return;
        Pokemon p = team.get(index);
        if (p != null && p.getSprite() != null && p.getSprite().getFrontDefault() != null) {
            slotImage.setImage(p.getSprite().getFrontDefault());
        }
    }

    @FXML private void onPartySlot1() {}
    @FXML private void onPartySlot2() {}
    @FXML private void onPartySlot3() {}
    @FXML private void onPartySlot4() {}
    @FXML private void onPartySlot5() {}
    @FXML private void onPartySlot6() {}
    @FXML private void onOppSlot1() {}
    @FXML private void onOppSlot2() {}
    @FXML private void onOppSlot3() {}
    @FXML private void onOppSlot4() {}
    @FXML private void onOppSlot5() {}
    @FXML private void onOppSlot6() {}

    @FXML private void onSwitchSlot1() {}
    @FXML private void onSwitchSlot2() {}
    @FXML private void onSwitchSlot3() {}
    @FXML private void onSwitchSlot4() {}
    @FXML private void onSwitchSlot5() {}
    @FXML private void onSwitchSlot6() {}

    @FXML private void onMove1Clicked() {}
    @FXML private void onMove2Clicked() {}
    @FXML private void onMove3Clicked() {}
    @FXML private void onMove4Clicked() {}

    @FXML private void onTabAttack() {}
    @FXML private void onTabSwitch() {}
    @FXML private void onTabItem() {}
    @FXML private void onTabForfeit() {}
}
