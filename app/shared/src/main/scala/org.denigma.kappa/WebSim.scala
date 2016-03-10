package org.denigma.kappa

object WebSim {

  trait WebSimMessage

  case class RunModel(code: String, nb_plot: Int = 1000, max_events: Option[Int], max_time: Option[Double] = None) extends WebSimMessage

  case class VersionInfo( build: String, version: String ) extends WebSimMessage

  case class SimulationStatus(
                               time_percentage: Option[Double],
                               event: Option[Int],
                               event_percentage: Option[Double],
                               tracked_events: Option[Int],
                               nb_plot: Option[Int],
                               max_time: Option[Int],
                               max_events: Option[Int],
                               is_running: Option[Boolean],
                               code: Option[String],
                               logMessages: Option[String],
                               plot: Option[KappaPlot],
                               flux_maps: Array[FluxMap]
                             )  extends WebSimMessage
  {
    def percentage: Double = event_percentage.orElse(time_percentage).get //showd throw if neither events not time are set
  }

  case class Observable(time: Double, values: Array[Double])  extends WebSimMessage

  case class KappaPlot(observables: Array[Observable]) extends WebSimMessage

  case class FluxData(flux_name: String) extends WebSimMessage

  case class FluxMap(flux_data: FluxData, flux_end: Double) extends WebSimMessage

}