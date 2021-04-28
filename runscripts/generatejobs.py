
jobtemplate = '''spark-submit --master yarn \\
--deploy-mode cluster \\
--driver-memory 16G --executor-memory 1G \\
--num-executors 18 --executor-cores 2 \\
--packages com.databricks:spark-xml_2.10:0.4.1 \\
--class ParseXML /home/hls327/BDAD_Violet_Noise/ScalaETL/target/scala-2.11/etl_2.11-0.1.jar traces/raw/week_%s_day_%s_trace.xml traces/processed/week_%s_day_%s_gps &\n'''


if __name__ == "__main__":
    count = 0
    with open("processxml.sh", "w") as f:
        for w in range(0,10):
            for d in range(0,7):
                count += 1
                outp = jobtemplate % (w, d, w, d)
                f.write(outp)

                if (count % 2) == 0:
                    f.write("wait\n")
                    f.write("sleep 30s\n")