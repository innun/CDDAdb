package manager

import org.scalatest.wordspec.AnyWordSpecLike

class MemResourceManagerTest extends AnyWordSpecLike {
  "_" in {
    val manager = new BaseResourceManager(
      poPath = "data/zh.po",
      jsonPath = List(
        //        "data/cdda/data/json/items",
        "data/cdda/data/json/monsters",
        "data/cdda/data/core"))

    manager.update()
  }
}
