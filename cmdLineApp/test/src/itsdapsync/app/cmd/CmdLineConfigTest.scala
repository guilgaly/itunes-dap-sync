package itsdapsync.app.cmd

import java.nio.file.Paths

import itsdapsync.testutils.UnitSpec

class CmdLineConfigTest extends UnitSpec {
  private val expectedConfig = CmdLineConfig(Paths.get("abc"), Paths.get("def"))

  "The config args parser" when {
    "given no args" should {
      "return None" in {
        CmdLineConfig.parse(Seq()) shouldBe None
      }
      "print error message" ignore {
        // TODO
      }
    }
    "given one arg" should {
      "return None" in {
        CmdLineConfig.parse(Seq("abc")) shouldBe None
      }
      "print error message" ignore {
        // TODO
      }
    }
    "given help option alone" should {
      "return None" in {
        CmdLineConfig.parse(Seq("--help")) shouldBe None
      }
      "print usage message" ignore {
        // TODO
      }
    }
    "given help option with two args" should {
      "return the parsed config" in {
        CmdLineConfig.parse(Seq("--help", "abc", "def")) shouldBe
          Some(expectedConfig)
      }
      "print usage message" ignore {
        // TODO
      }
    }
    "given two args" should {
      "return the parsed config" in {
        CmdLineConfig.parse(Seq("abc", "def")) shouldBe Some(expectedConfig)
      }
      "print nothing" ignore {
        // TODO
      }
    }
    "given too many args" should {
      "return None" in {
        CmdLineConfig.parse(Seq("abc", "def", "ghi")) shouldBe None
      }
      "print error message" ignore {
        // TODO
      }
    }
    "given non-existant option" should {
      "return None" in {
        CmdLineConfig.parse(Seq("--foo", "abc", "def")) shouldBe None
      }
      "print error message" ignore {
        // TODO
      }
    }
  }
}
