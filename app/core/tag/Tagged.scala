package core

/*
 * This package allows us to use primitives in a strongly typed fashion
 * (http://eed3si9n.com/learning-scalaz/Tagged+type.html)
 */

package object tag {
  type Tagged[U] = { type Tag = U }
  type @@[T, U] = T with Tagged[U]

  /* Tag a String with an arbitrary type */
  def tag[U](str: String): String @@ U = str.asInstanceOf[String @@ U]

  /* Tag an Int with an arbitrary type */
  def tag[U](int: Int): Int @@ U = int.asInstanceOf[Int @@ U]
}
