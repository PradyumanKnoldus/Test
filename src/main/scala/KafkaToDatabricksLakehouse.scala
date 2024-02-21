import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.{DataFrame, SparkSession}

object KafkaToDatabricksLakehouse extends App {
  // Define the SparkSession
  private val spark = SparkSession.builder()
    .appName("Kafka to Databricks Lakehouse")
    .master("local[*]")
    .getOrCreate()

  //  import spark.implicits._
  //
  // Define the Kafka server location
  private val kafkaServer = "localhost:9092"
  // Define the Kafka topic to consume data from
  private val kafkaTopic = "test_topic"

  // Define the Kafka consumer
  private val kafkaDF = spark.readStream
    .format("kafka")
    .option("inferSchema", "true")
    .option("kafka.bootstrap.servers", kafkaServer)
    .option("subscribe", kafkaTopic)
    .load()

  kafkaDF.printSchema()

  //  // Define the schema for the data
  //  private val schema = "first_name STRING, last_name STRING, location STRING, online BOOLEAN, followers INT"
  //
  //  // Process the consumed Kafka data
  //  val processedDF = kafkaDF.selectExpr("CAST(value AS STRING)")
  //    .as[String]
  //    .select(from_json($"value", schema).as("data"))
  //    .select("data.*")
  //
  // Define the Databricks Lakehouse configuration
  private val lakehouseAccessToken = "dapi02a7635ff02b96ddaf1496a3ef80059e"
  private val lakehouseURL = "https://adb-2921883501836317.17.azuredatabricks.net"

  // Send processed data to Databricks Lakehouse
  private val lakehouseWriter = kafkaDF.writeStream
    .trigger(Trigger.ProcessingTime("10 seconds"))
    .foreachBatch { (batchDF: DataFrame, batchId: Long) =>
      batchDF.write
        .format("parquet")
        .mode("append")
        .option("inferSchema", "true")
        .option("token", lakehouseAccessToken)
        .option("endpoint", lakehouseURL)
        .save("/FileStore/tables")
    }
    .start()

  // Wait for the stream to finish
  lakehouseWriter.awaitTermination()
}
