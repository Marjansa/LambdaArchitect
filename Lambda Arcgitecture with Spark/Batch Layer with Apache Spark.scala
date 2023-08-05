import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._

object BatchLayerExample {
  def main(args: Array[String]): Unit = {
    // Create a SparkSession
    val spark = SparkSession.builder()
      .appName("BatchLayerExample")
      .getOrCreate()

    // Read the CSV file into a DataFrame
    val salesDF: DataFrame = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("path/to/sales_data.csv")

    // Perform batch processing to calculate total revenue by product
    val revenueByProductDF: DataFrame = salesDF
      .groupBy("product_id", "product_name")
      .agg(sum(col("quantity") * col("price")).alias("total_revenue"))

    // Display the results
    revenueByProductDF.show()

    // Save the results to a new CSV file
    revenueByProductDF
      .write
      .option("header", "true")
      .csv("path/to/revenue_by_product.csv")

    // Stop the SparkSession
    spark.stop()
  }
}
