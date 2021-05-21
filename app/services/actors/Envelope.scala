package services.actors


case class Envelope[I, P](id: I, payload: P)