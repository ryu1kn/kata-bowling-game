﻿using System;
using System.Collections.Generic;
using System.Linq;

namespace ExtensibleCardGame
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Hello World!");
        }
    }

    public class App
    {
        public static Func<string, int> EvaluateHand = game => {
            try {
                return new Game(game).Evaluate();
            } catch (InvalidCardException) {
                return 0;
            }
        };
    }

    class Game {
        private readonly bool customRule;
        private readonly List<Card> cards;

        public Game(string game)
        {
            var parts = game.Split(';');
            if (parts.Length > 1) {
                customRule = true;
                cards = ToCards(parts[1]);
            } else {
                customRule = false;
                cards = ToCards(parts[0]);
            }
        }

        private List<Card> ToCards(string cards) =>
            cards.Split(',').Select(Card.From).ToList();

        public int Evaluate() =>
            !customRule ? cards.Select(c => c.Value).Sum() : 30;
    }

    readonly struct Card {
        public readonly int Value;

        Card(int value) {
            this.Value = value;
        }

        static public Func<string, Card> From = card => {
            var value = int.Parse(card.Remove(card.Length - 1));
            if (value > 13) throw new InvalidCardException();
            return new Card(value);
        };
    }

    class InvalidCardException : Exception {}
}
