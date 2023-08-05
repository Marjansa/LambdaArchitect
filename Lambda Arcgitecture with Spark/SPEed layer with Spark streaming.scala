import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._
import org.apache.spark.streaming.{StreamingContext, Seconds}

object SpeedLayerExample {
  def main(args: Array[String]): Unit = {
    // Create a SparkSession
    val spark = SparkSession.builder()
      .appName("SpeedLayerExample")
      .getOrCreate()

    // Create a StreamingContext with a batch interval of 5 seconds
    val ssc = new StreamingContext(spark.sparkContext, Seconds(5))

    // Read the sales data from the stream (you can use Kafka, Flume, etc. in a real scenario)
    val salesStream = ssc.socketTextStream("localhost", 9999)

    // Define a case class to parse the sales data from the stream
    case class SalesData(product_id: Int, product_name: String, quantity: Int, price: Double)

    // Transform the stream into a DataFrame using the case class
    val salesDF: DataFrame = salesStream.map { line =>
      val Array(product_id, product_name, quantity, price) = line.split(",")
      SalesData(product_id.toInt, product_name, quantity.toInt, price.toDouble)
    }.toDF()

    // Perform real-time processing to calculate total revenue by product
    val revenueByProductDF: DataFrame = salesDF
      .groupBy("product_id", "product_name")
      .agg(sum(col("quantity") * col("price")).alias("total_revenue"))

    // Display the results
    revenueByProductDF.writeStream
      .outputMode("complete")
      .format("console")
      .start()

    // Start the streaming context
    ssc.start()
    ssc.awaitTermination()
  }
}
