package handler

import common.Field._
import common.Type
import manager.HandlerContext
import play.api.Logger
import play.api.libs.json.{JsArray, JsObject, Json}
import utils.I18nUtil.tranObj
import utils.JsonUtil._

import scala.collection.mutable

object ItemHandler {
  private val log = Logger(ItemHandler.getClass)

  def handle(json: JsObject): JsObject = {
        implicit var pend: JsObject = json
//        val tp = getString(TYPE).toLowerCase
    //        pend = handleColor
    //        pend = handleConductive
    //
    //        // handle special field according to subtype
    //        pend = tp match {
    //          case Type.ARMOR => calArmorProtection
    //          case _ => pend
    //        }
      pend
    }

  private def calArmorProtection(implicit obj: JsObject, ctxt: HandlerContext): JsObject = {
    def baseResit(rType: String, mts: JsArray): Double =
      mts.value.foldLeft(0.0) {
        (res, mtId) => {
          val mtObj = ctxt.objCache(Type.MATERIAL)(mtId.as[String])
          val r = getNumber(rType)(mtObj)
          res + r.toInt
        }
      } / mts.value.length

    def pResist(rType: String, thickness: Int, mts: JsArray): Long =
      math.round(baseResit(rType, mts) * thickness)

    def eResist(rType: String, ep: Int, mts: JsArray): Long =
      if (ep < 10)
        math.round(baseResit(rType, mts) * ep / 10)
      else
        math.round(baseResit(rType, mts))

    val thickness = getNumber(MATERIAL_THICKNESS).toInt
    val ep = getNumber(ENVIRONMENTAL_PROTECTION).toInt
    val mts = getArray(MATERIAL)
    val bashRs = pResist(BASH_RESIST, thickness, mts)
    val cutRs = pResist(CUT_RESIST, thickness, mts)
    val bulletRs = pResist(BULLET_RESIST, thickness, mts)
    val acidRs = eResist(ACID_RESIST, ep, mts)
    val fireRs = eResist(FIRE_RESIST, ep, mts)

    obj ++ Json.obj(
      BASH_RESIST -> bashRs,
      CUT_RESIST -> cutRs,
      BULLET_RESIST -> bulletRs,
      ACID_RESIST -> acidRs,
      FIRE_RESIST -> fireRs,
      ENVIRONMENTAL_PROTECTION -> ep
    )
  }

  private def handleConductive(implicit obj: JsObject, ctxt: HandlerContext): JsObject = {
    val con = if (hasFlag("CONDUCTIVE"))
      true
    else if (hasFlag("NONCONDUCTIVE"))
      false
    else {
      getArray(MATERIAL).value.foldLeft(false) {
        (res, mtId) =>
          val mtObj = ctxt.objCache(Type.MATERIAL)(mtId.as[String])
          val elecRs = getNumber(ELEC_RESIST)(mtObj).toInt
          if (elecRs <= 1 && !res) true else res
      }
    }
    obj ++ Json.obj(CONDUCTIVE -> con)
  }
}
