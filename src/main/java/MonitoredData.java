




import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonitoredData {
    private String startTime;
    private String endTime;
    private String activity;
    public MonitoredData()
    {}
    public MonitoredData(String startTime,String endTime,String activity)
    {
        this.startTime=startTime;
        this.endTime=endTime;
        this.activity=activity;

    }
    public void readDataFromFile() throws IOException {

        //TASK 1
        String fileName= "Activities.txt";
        Stream<String> stringStream= Files.lines(Paths.get(fileName));
        List<MonitoredData> monitoredData=stringStream.map(line->line.split("\\t\\t")).map(data->new MonitoredData(data[0],data[1],data[2])).collect(Collectors.toList());

        this.writeTask1(monitoredData);
        this.task2(monitoredData);
        writeTask3(this.task3(monitoredData));
        this.task5(monitoredData);
        this.task6(monitoredData);
        this.task4(monitoredData);
    }

    public long task2(List<MonitoredData> monitoredData) throws IOException {
        List<String> endTimesString=monitoredData.stream().map(MonitoredData::getEndTime).collect(Collectors.toList());
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<LocalDateTime> endTimes=endTimesString.stream().map(data->LocalDateTime.parse(data,formatter)).collect(Collectors.toList());
        List<Integer> numDays=endTimes.stream().map(day->day.getDayOfMonth()).collect(Collectors.toList());

        long numberOfDistinctDays=numDays.stream().distinct().count();
        FileWriter fileWriter=new FileWriter("task_2.txt");
        fileWriter.write(Long.toString(numberOfDistinctDays)+" zile distincte au fost numarate in fisierul Activities.txt");
        fileWriter.close();
        return numberOfDistinctDays;
    }

    public Map<String,Long> task3(List<MonitoredData> monitoredData)
    {
        List<String> activitiesNames=monitoredData.stream()
                .map(MonitoredData::getActivity)
                .collect(Collectors.toList()) ;
        Map<String,Long> countActivities=activitiesNames.stream().collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
        return countActivities;
    }

    public int getDurationOfActivity(String s,List<MonitoredData> monitoredData)
    {
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for(MonitoredData i:monitoredData)
        { i.setActivity(i.getActivity().split("\t")[0]); }
        int result=0;
        for(MonitoredData i:monitoredData)
        {
            if (i.getActivity().equals(s))
            {
                LocalDateTime timeEnd=LocalDateTime.parse(i.getEndTime(),formatter);
                LocalDateTime timeBegin= LocalDateTime.parse(i.getStartTime(),formatter);
                result+=Duration.between(timeBegin,timeEnd).toMinutes();
            }
        }
        return result;
    }

    public long getDurationOfActivityLine(String s, List<MonitoredData> monitoredData)
    {
        long result=0;
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for(MonitoredData i:monitoredData)
        { i.setActivity(i.getActivity().split("\t")[0]); }
        for(MonitoredData i:monitoredData)
        {
            if (i.getActivity().equals(s))
            {
                LocalDateTime timeEnd=LocalDateTime.parse(i.getEndTime(),formatter);
                LocalDateTime timeBegin= LocalDateTime.parse(i.getStartTime(),formatter);
                result=Duration.between(timeBegin,timeEnd).toMinutes();
                break;
            }
        }
        return result;
    }
    public boolean hasMoreThan90PercentOccurence(String s,List<MonitoredData> monitoredData)
    {
        boolean hasMore=false;
        double occurNum=0;
        Map<String,Long> countMap=this.task3(monitoredData);
        for(MonitoredData i:monitoredData)
        { i.setActivity(i.getActivity().split("\t")[0]); }
        for(MonitoredData i:monitoredData)
        {
            if (i.getActivity().equals(s) && getDurationOfActivityLine(s,monitoredData)<5)
            {
                Long appearanceNum=countMap.get(s);
                occurNum++;
                Double result=occurNum/appearanceNum.intValue();
                if (result>0.9)
                {
                    hasMore=true;
                    break;
                }
            }
        }
        return hasMore;
    }

    public void task4(List<MonitoredData> monitoredData) throws IOException {
        DateTimeFormatter formatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<Integer,Map<String,Long>> countActivitiesMap=monitoredData.stream().
                collect(Collectors.groupingBy(data-> LocalDateTime.parse(data.getStartTime(),formatter).getDayOfMonth(),
                        Collectors.groupingBy(MonitoredData::getActivity,Collectors.counting())));
        writeTask4(countActivitiesMap);
    }

    public Map<String,Integer> task5(List<MonitoredData> monitoredData) throws IOException {
        Map<String,Integer> mapCounts=monitoredData.stream().
                collect(Collectors.toMap(data->data.getActivity(),data->getDurationOfActivity(data.getActivity(),monitoredData),(data1,data2)->data1));
        writeTask5(mapCounts);
        return mapCounts;
    }

    public  List<String> task6(List<MonitoredData> monitoredData) throws IOException {
       List<MonitoredData> auxList=monitoredData.stream().
               filter(data->data.hasMoreThan90PercentOccurence(data.getActivity(),monitoredData)).
               collect(Collectors.toList());
       List<String> auxProcList=auxList.stream().map(data->data.getActivity()).collect(Collectors.toList());
       List<String> finalList=auxProcList.stream().distinct().collect(Collectors.toList());
       writeTask6(finalList);
       return  finalList;
    }

    public void writeTask6(List<String> finalList) throws IOException {
        FileWriter fileWriter=new FileWriter("task_6.txt");
        String toWrite="";
        for(String i:finalList)
        {
            toWrite+=i+" ";
        }
        fileWriter.write(toWrite);
        fileWriter.close();
    }
    public void writeTask4(Map<Integer,Map<String,Long>> countActivitiesMap) throws IOException {
        FileWriter fileWriter=new FileWriter("task_4.txt");
        String toWrite="";
        for(Map.Entry<Integer, Map<String,Long>> entry: countActivitiesMap.entrySet())
        {
            toWrite+="Ziua "+entry.getKey()+" "+entry.getValue().entrySet()+" \n";
        }
        fileWriter.write(toWrite);
        fileWriter.close();
    }
    public void writeTask5(Map<String,Integer> mapCounts) throws IOException {
        FileWriter fileWriter=new FileWriter("task_5.txt");
        String toWrite="";
        for(Map.Entry<String,Integer> entry:mapCounts.entrySet())
        {
            toWrite+=entry.getKey()+" "+entry.getValue()+" \n";
        }
        fileWriter.write(toWrite);
        fileWriter.close();
    }
    public void writeTask3(Map<String,Long> countActivities) throws IOException {
        FileWriter fileWriter=new FileWriter("task_3.txt");
        String toWrite="";
        for(Map.Entry<String,Long> entry:countActivities.entrySet())
        {
            toWrite+=entry.getKey()+" "+entry.getValue()+" \n";
        }
        fileWriter.write(toWrite);
        fileWriter.close();
    }
    public void writeTask1(List<MonitoredData> monitoredData) throws IOException {
        FileWriter fileWriter=new FileWriter("task_1.txt");
        String toWrite="";
        for(MonitoredData i:monitoredData)
        {
            toWrite+=i.getActivity()+"          "+i.getStartTime()+"         "+i.getEndTime()+" \n";
        }
        fileWriter.write(toWrite);
        fileWriter.close();
    }
    public String getStartTime(){return this.startTime;}
    public void setActivity(String s)
    {
        this.activity=s;
    }

    public String getActivity() {
        return activity;
    }

    public String getEndTime() {
        return endTime;
    }

}
