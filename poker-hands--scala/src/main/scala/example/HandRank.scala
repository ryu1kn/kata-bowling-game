package example

import example.Util.forallNeighbours
import Hand._

object HandRank {
  val ranks = List(
    StraightFlush,
    FourOfAKind,
    FullHouse,
    Flush,
    Straight,
    ThreeOfAKind,
    TwoPairs,
    Pair,
    HighCard
  )

  def compare(rank1: HandRank, rank2: HandRank): Int =
    ranks.indexOf(rank2).compare(ranks.indexOf(rank1))
}

sealed trait HandRank {
  val name: String

  def isOfRank(cards: List[Card]): Boolean

  def compare(hand: Hand, that: Hand): Int

  def description(hand: Hand): String
}

case object StraightFlush extends HandRank {
  override val name: String = "straight flush"

  override def isOfRank(cards: List[Card]): Boolean =
    forallNeighbours(cards, isSameSuiteIncrement)

  private def isSameSuiteIncrement(card1: Card, card2: Card): Boolean =
    card2.suite == card1.suite && card2.intValue == card1.intValue + 1

  override def compare(hand1: Hand, hand2: Hand): Int = lastCard(hand1).compare(lastCard(hand2))

  override def description(hand: Hand): String = lastCard(hand).valueName
}

case object FourOfAKind extends HandRank {
  override val name: String = "four of a kind"

  override def isOfRank(cards: List[Card]): Boolean = sameNumberGroups(cards) match {
    case List((4, _), _) => true
    case _ => false
  }

  override def description(hand: Hand): String = findMostCommonNumberCard(hand.cards).valueName

  override def compare(hand1: Hand, hand2: Hand): Int =
    findMostCommonNumberCard(hand1.cards).compare(findMostCommonNumberCard(hand2.cards))
}

case object FullHouse extends HandRank {
  override val name: String = "full house"

  override def isOfRank(cards: List[Card]): Boolean = sameNumberGroups(cards) match {
    case List((3, _), (2, _)) => true
    case _ => false
  }

  override def description(hand: Hand): String = sameNumberGroups(hand.cards) match {
    case List((3, cardName1), (2, cardName2)) => s"$cardName1 over $cardName2"
  }

  override def compare(hand1: Hand, hand2: Hand): Int =
    findMostCommonNumberCard(hand1.cards).compare(findMostCommonNumberCard(hand2.cards))
}

case object Flush extends HandRank {
  override val name: String = "flush"

  override def isOfRank(cards: List[Card]): Boolean = cards.groupBy[Char](card => card.suite).size == 1
  override def description(hand: Hand): String = lastCard(hand).valueName

  override def compare(hand1: Hand, hand2: Hand): Int =
    hand1.cards.zip(hand2.cards).foldRight(0) {
      case ((cardA, cardB), 0) => cardA compare cardB
      case (_, n) => n
    }
}

case object Straight extends HandRank {
  override val name: String = "straight"

  override def isOfRank(cards: List[Card]): Boolean =
    forallNeighbours[Int](cards.map(_.intValue).sorted, (card1, card2) => card2 == card1 + 1)
  override def description(hand: Hand): String = lastCard(hand).valueName
  override def compare(hand1: Hand, hand2: Hand): Int = lastCard(hand1).compare(lastCard(hand2))
}

case object ThreeOfAKind extends HandRank {
  override val name: String = "three of a kind"

  override def isOfRank(cards: List[Card]): Boolean = sameNumberGroups(cards) match {
    case List((3, _), _*) => true
    case _ => false
  }

  override def description(hand: Hand): String = findMostCommonNumberCard(hand.cards).valueName

  override def compare(hand1: Hand, hand2: Hand): Int =
    findMostCommonNumberCard(hand1.cards).compare(findMostCommonNumberCard(hand2.cards))
}

case object TwoPairs extends HandRank {
  override val name: String = "two pairs"

  override def isOfRank(cards: List[Card]): Boolean = sameNumberGroups(cards) match {
    case List((2, _), (2, _), _) => true
    case _ => false
  }
  override def description(hand: Hand): String = findMostCommonNumberCard(hand.cards).valueName

  override def compare(hand1: Hand, hand2: Hand): Int =
    findMostCommonNumberCard(hand1.cards).compare(findMostCommonNumberCard(hand2.cards))
}

case object Pair extends HandRank {
  override val name: String = "pair"

  override def isOfRank(cards: List[Card]): Boolean = sameNumberGroups(cards) match {
    case List((2, _), _*) => true
    case _ => false
  }

  override def description(hand: Hand): String = findMostCommonNumberCard(hand.cards).valueName

  override def compare(hand1: Hand, hand2: Hand): Int =
    findMostCommonNumberCard(hand1.cards).compare(findMostCommonNumberCard(hand2.cards))
}

case object HighCard extends HandRank {
  override val name: String = "high card"

  override def isOfRank(cards: List[Card]): Boolean = true
  override def description(hand: Hand): String = lastCard(hand).valueName

  override def compare(hand1: Hand, hand2: Hand): Int = lastCard(hand1).compare(lastCard(hand2))
}
