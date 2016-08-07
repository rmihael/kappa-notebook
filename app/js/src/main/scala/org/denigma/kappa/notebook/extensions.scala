package org.denigma.kappa.notebook

import java.nio.ByteBuffer

import rx._

import scala.collection.immutable._
import fastparse.all.Parsed
import rx.Ctx.Owner.Unsafe.Unsafe
import org.denigma.binding.extensions._
import org.scalajs.dom.{Event, File, FileList, FileReader}
import org.scalajs.dom.ext.EasySeq
import org.scalajs.dom.raw.{Blob, Element, HTMLElement, ProgressEvent}

import scala.concurrent.{Future, Promise}
import scala.scalajs.js.typedarray.{ArrayBuffer, TypedArrayBuffer}

object extensions extends SharedExtensions{

  implicit class BlobOps(blob: Blob) {

    def readAsByteBuffer: Future[ByteBuffer] = {
      val result = Promise[ByteBuffer]
      val reader = new FileReader()
      def onLoadEnd(ev: ProgressEvent): Any = {
        val buff = reader.result.asInstanceOf[ArrayBuffer]
        val bytes = TypedArrayBuffer.wrap(buff)
        result.success(bytes)
      }
      def onErrorEnd(ev: Event): Any = {
        result.failure(new Exception("READING FAILURE " + ev.toString))
      }
      reader.onloadend = onLoadEnd _
      reader.onerror = onErrorEnd _
      reader.readAsArrayBuffer(blob)
      result.future
    }

  }

  implicit class ParsedExt[T](source: Parsed[T]) {


    def map[R](fun: T => R): Parsed[R] = source match {
      case Parsed.Success(value, index) => Parsed.Success[R](fun(value),index)
      case f:Parsed.Failure => f
    }

    def recover(recoverFun: String => Parsed[T]): Parsed[T] = source match {
      case f: Parsed.Failure => recoverFun(f.extra.input)
      case other => other
    }


    def onSuccess(fun: T => Unit): Parsed[T] = source match {
      case s @ Parsed.Success(value, index) =>
        fun(s.value)
        s
      case f:Parsed.Failure => f
    }

    def onFailure(recover: String => Unit): Parsed[T] = source match {
      case s @ Parsed.Success(value, index) => s
      case f:Parsed.Failure =>
        recover(f.extra.input)
        f
    }
  }
}
