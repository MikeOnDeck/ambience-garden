package charmony.api.chest_puzzles;

import charmony.api.stone_chests.StoneChestMaterial;

@SuppressWarnings("unused")
public interface ChestPuzzleMenu {
    StoneChestMaterial getMaterial();

    ChestPuzzleType puzzleType();
}
