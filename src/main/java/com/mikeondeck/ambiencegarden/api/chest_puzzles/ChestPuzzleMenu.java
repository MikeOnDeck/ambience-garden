package com.mikeondeck.ambiencegarden.api.chest_puzzles;

import com.mikeondeck.ambiencegarden.api.stone_chests.StoneChestMaterial;

@SuppressWarnings("unused")
public interface ChestPuzzleMenu {
    StoneChestMaterial getMaterial();

    ChestPuzzleType puzzleType();
}
