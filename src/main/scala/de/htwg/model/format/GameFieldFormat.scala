package de.htwg.model.format

import de.htwg.model.GameField
import de.htwg.model.Player
import de.htwg.model.GridPosition
import play.api.libs.json.OFormat
import play.api.libs.json.JsObject
import play.api.libs.json.JsValue
import play.api.libs.json.JsResult
import play.api.libs.json.Json
import play.api.libs.json.Reads

object GameFieldFormat extends OFormat[GameField] {

  private val posLit = "pos"
  private val playerLit = "player"
  private def writeGameFieldEntry(pair: (GridPosition, Player)): JsObject = {
    val (pos, p) = pair
    Json.obj(
      posLit -> GridPositionFormat.writes(pos),
      playerLit -> PlayerFormat.writes(p)
    )
  }

  private def readGameFieldEntry: Reads[(GridPosition, Player)] = Reads { js =>
    for {
      pos <- js.\(posLit).validate(GridPositionFormat)
      p <- js.\(playerLit).validate(PlayerFormat)
    } yield {
      pos -> p
    }

  }

  private val finishedLit = "isFinished"
  private val currentPlayerLit = "currentPlayer"
  private val fieldLit = "field"
  override def writes(o: GameField): JsObject = {
    Json.obj(
      finishedLit -> o.isFinished,
      currentPlayerLit -> PlayerFormat.writes(o.current),
      fieldLit -> o.gameField.toList.map(writeGameFieldEntry),
    )
  }


  override def reads(json: JsValue): JsResult[GameField] = {
    for {
      finished <- json.\(finishedLit).validate[Boolean]
      currentPlayer <- json.\(currentPlayerLit).validate(PlayerFormat)
      field <- json.\(fieldLit).validate(Reads.list(readGameFieldEntry))
    } yield {
      new GameField(currentPlayer, field.toMap, finished)
    }
  }
}
