import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;


public class OnlineCoursesAnalyzer {

    String datasetPath;

    public OnlineCoursesAnalyzer(String datasetPath) {
        this.datasetPath = datasetPath;
    }

    public Map<String, Integer> getPtcpCountByInst() {
        Map<String, Integer> ptcpCountByInst = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(datasetPath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr)) {

            String line;
            line = reader.readLine();
            String[] titles = line.split(",");
            String institution = "Institution";
            String count = "Participants (CourseBF Content Accessed)";
            int ins = 0;
            int cnt = 8;
            for (int i = 0; i < titles.length; i++) {
                if (titles[i].equals("Institution")) {
                    ins = i;
                } else if (titles[i].equals("Participants (CourseBF Content Accessed)")) {
                    cnt = i;
                }
            }

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                for (String column : columns) {
                    column.replaceAll("^\"|\"$", "");
                }

//                String[] columns = line.split(",");
                String temp_s = columns[ins];
                int temp_i = Integer.parseInt(columns[cnt]);

                if (ptcpCountByInst.containsKey(temp_s)) {
                    ptcpCountByInst.put(temp_s, ptcpCountByInst.get(temp_s) + temp_i);
                } else {
                    ptcpCountByInst.put(temp_s, temp_i);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Integer> sortedMap = ptcpCountByInst.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return sortedMap;
    }

    public Map<String, Integer> getPtcpCountByInstAndSubject() {
        Map<String, Integer> ptcpCountByInstAndSubject = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(datasetPath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr)) {

            String line;
            line = reader.readLine();
            String[] titles = line.split(",");
            String institution = "Institution";
            String subject = "CourseBF Subject";
            String count = "Participants (CourseBF Content Accessed)";
            int ins = 0;
            int sub = 5;
            int cnt = 8;
            for (int i = 0; i < titles.length; i++) {
                if (titles[i].equals("Institution")) {
                    ins = i;
                } else if (titles[i].equals("CourseBF Subject")) {
                    sub = i;
                } else if (titles[i].equals("Participants (CourseBF Content Accessed)")) {
                    cnt = i;
                }
            }

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                for (String column : columns) {
                    column.replaceAll("^\"|\"$", "");
                }

//                String[] columns = line.split(",");
                String temp_i = columns[ins];
                String temp_s = columns[sub];
//                String[] t = temp_s.split(",\\s*");
                int temp_c = Integer.parseInt(columns[cnt]);
//                for(String temp: t) {
                temp_s = temp_s.replace("\"", "");
//                    temp = temp.replace("and ","");
                String ins_sub = temp_i + "-" + temp_s;

                if (ptcpCountByInstAndSubject.containsKey(ins_sub)) {
                    ptcpCountByInstAndSubject.put(ins_sub,
                        ptcpCountByInstAndSubject.get(ins_sub) + temp_c);
                } else {
                    ptcpCountByInstAndSubject.put(ins_sub, temp_c);
                }
//                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, Integer> sortedMap = ptcpCountByInstAndSubject.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return sortedMap;
    }

    public Map<String, List<List<String>>> getCourseListOfInstructor() {
        Map<String, List<List<String>>> courseListOfInstructor = new HashMap<>();
//        Map<String,List<String>> test = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(datasetPath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr)) {

            String line;
            line = reader.readLine();
            String[] titles = line.split(",");
            int ins = 4;
            int cou = 3;
            for (int i = 0; i < titles.length; i++) {
                if (titles[i].equals("Instructors")) {
                    ins = i;
                } else if (titles[i].equals("CourseBF Title")) {
                    cou = i;
                }
            }

            Comparator<String> comparator = (str1, str2) -> {
                return str1.compareTo(str2);
            };

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
//                for (String column : columns) {
//                    column.replaceAll("^\"|\"$", "");
//                }

                String temp_i = columns[ins];
                String temp_c = columns[cou];
                String[] t = temp_i.split(",\\s*");

                for (String temp : t) {
                    temp = temp.replace("\"", "");

                    if (!courseListOfInstructor.containsKey(temp)) {
                        List<String> list1 = new ArrayList<>();
                        List<String> list2 = new ArrayList<>();
                        List<List<String>> courseList = new ArrayList<>();
                        courseList.add(list1);
                        courseList.add(list2);
                        courseListOfInstructor.put(temp,
                            courseList);
                    }
                    temp_c = temp_c.replace("\"", "");

//                    if(test.containsKey(temp)){
//                        List<String> past = test.get(temp);
//                        past.add(temp_c);
//                    }else {
//                        List<String> begin = new ArrayList<>();
//                        begin.add(temp_c);
//                        test.put(temp, begin);
//                    }

                    if (t.length == 1) {
                        List<String> independentCourses = courseListOfInstructor.get(temp).get(0);
                        if (!independentCourses.contains(temp_c)) {
                            independentCourses.add(temp_c);
                        }
                        Collections.sort(independentCourses, comparator);
                    } else {
                        List<String> coDevelopedCourses = courseListOfInstructor.get(temp).get(1);
                        if (!coDevelopedCourses.contains(temp_c)) {
                            coDevelopedCourses.add(temp_c);
                        }
                        Collections.sort(coDevelopedCourses, comparator);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return courseListOfInstructor;
    }

    public List<String> getCourses(int topK, String by) {
        List<String> courses = new ArrayList<>();
        Map<String, Double> hours = new HashMap<>();
        Map<String, Integer> participants = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(datasetPath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr)) {

            String line;
            line = reader.readLine();
            String[] titles = line.split(",");
            String search = "";
            if (by.equals("hours")) {
                search = "Total CourseBF Hours (Thousands)";
            } else if (by.equals("participants")) {
                search = "Participants (CourseBF Content Accessed)";
            }
            int s = 0;
            int cou = 3;
            for (int i = 0; i < titles.length; i++) {
                if (titles[i].equals(search)) {
                    s = i;
                } else if (titles[i].equals("CourseBF Title")) {
                    cou = i;
                }
            }

            int count = 0;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
//                for (String column : columns) {
//                    column.replaceAll("^\"|\"$", "");
//                }

                String temp = columns[cou];
                temp.replace("\"", "");
                if (!courses.contains(temp)) {
                    courses.add(temp);
                }

                if (by.equals("hours")) {
                    if (hours.containsKey(temp)) {
                        hours.put(temp, Math.max(hours.get(temp), Double.parseDouble(columns[s])));
                    } else {
                        hours.put(temp, Double.parseDouble(columns[s]));
                    }
//                    hours.put(temp,Double.parseDouble(columns[s]));
                } else if (by.equals("participants")) {
                    if (participants.containsKey(temp)) {
                        participants.put(temp,
                            Math.max(participants.get(temp), Integer.parseInt(columns[s])));
                    } else {
                        participants.put(temp, Integer.parseInt(columns[s]));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //sort
        courses.sort((c1, c2) -> {
            double diff = 0;
            if (by.equals("hours")) {
                diff = hours.get(c2) - hours.get(c1);
            } else if (by.equals("participants")) {
                diff = participants.get(c2) - participants.get(c1);
            }
            if (diff < 0) {
                return -1;
            } else if (diff > 0) {
                return 1;
            } else {
                return c1.compareTo(c2);
            }
        });

        List<String> topKcourses = new ArrayList<>();
        List<Integer> p = new ArrayList<>();
        int count = 0;
        for (String top : courses) {
            if (count < topK) {
                topKcourses.add(top);
                p.add(participants.get(top));
                count++;
            } else {
                break;
            }
        }

        return topKcourses;
    }

    public List<String> searchCourses(String courseSubject, double percentAudited,
        double totalCourseHours) {
        List<String> search = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(datasetPath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr)) {

            String line;
            line = reader.readLine();
            String[] titles = line.split(",");
            int tit = 3;
            int sub = 5;
            int per = 11;
            int tot = 17;
            for (int i = 0; i < titles.length; i++) {
                if (titles[i].equals("CourseBF Title")) {
                    tit = i;
                } else if (titles[i].equals("CourseBF Subject")) {
                    sub = i;
                } else if (titles[i].equals("% Audited")) {
                    per = i;
                } else if (titles[i].equals("Total CourseBF Hours (Thousands)")) {
                    tot = i;
                }
            }

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
//                for (String column : columns) {
//                    column.replaceAll("^\"|\"$", "");
//                }
                String title = columns[tit];
                title = title.replace("\"", "");
                String temp_s = columns[sub];
                double temp_p = Double.parseDouble(columns[per]);
                double temp_t = Double.parseDouble(columns[tot]);
                String[] t = temp_s.toLowerCase().split(",\\s*");
                courseSubject = courseSubject.toLowerCase();

                int flag = 0;
                for (String temp : t) {
                    temp = temp.replace("\"", "");
                    temp = temp.replace("and ", "");
                    if (temp.contains(courseSubject)) {
                        flag++;
                        break;
                    }
                }
                if (temp_p >= percentAudited) {
                    flag++;
                }
                if (temp_t <= totalCourseHours) {
                    flag++;
                }
                if (flag == 3 && !search.contains(title)) {
                    search.add(title);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(search);

        return search;
    }

    public List<String> recommendCourses(int age, int gender, int isBachelorOrHigher) {
        List<String> recommend = new ArrayList<>();
        Map<Map<String, Double>, String> search = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(datasetPath);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr)) {
            String line;
            line = reader.readLine();
            List<Course> courses = new ArrayList<>();
            Map<String, Course> courses_id = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                String[] info = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                Course course = new Course(info[0], info[1], new Date(info[2]), info[3], info[4],
                    info[5],
                    Integer.parseInt(info[6]), Integer.parseInt(info[7]), Integer.parseInt(info[8]),
                    Integer.parseInt(info[9]), Integer.parseInt(info[10]),
                    Double.parseDouble(info[11]),
                    Double.parseDouble(info[12]), Double.parseDouble(info[13]),
                    Double.parseDouble(info[14]),
                    Double.parseDouble(info[15]), Double.parseDouble(info[16]),
                    Double.parseDouble(info[17]),
                    Double.parseDouble(info[18]), Double.parseDouble(info[19]),
                    Double.parseDouble(info[20]),
                    Double.parseDouble(info[21]), Double.parseDouble(info[22]));

                course.sumAge = course.medianAge;
                course.sumMale = course.percentMale;
                course.sumDegree = course.percentDegree;
                course.num++;

                if (courses_id.containsKey(course.number)) {
                    Course old = courses_id.get(course.number);
                    if ((!course.title.equals(old.title)) && (
                        course.launchDate.compareTo(old.launchDate) > 0)) {
                        course.sumAge = old.sumAge + course.sumAge;
                        course.sumMale = old.sumMale + course.sumMale;
                        course.sumDegree = old.sumDegree + course.sumDegree;
                        course.num = old.num + course.num;
                        courses.remove(old);
                        courses.add(course);
                        courses_id.remove(old);
                        courses_id.put(course.number, course);
                    }else{
                        old.sumAge = old.sumAge + course.sumAge;
                        old.sumMale = old.sumMale + course.sumMale;
                        old.sumDegree = old.sumDegree + course.sumDegree;
                        old.num = old.num + course.num;
                    }
                } else {
                    courses_id.put(course.number, course);
                    courses.add(course);
                }
            }
            for (Course course : courses) {
                double value = Math.pow((age - course.sumAge/course.num), 2)
                    + Math.pow((gender * 100 - course.sumMale/course.num), 2)
                    + Math.pow((isBachelorOrHigher * 100 - course.sumDegree/course.num), 2);
                course.value = value;
            }
            Collections.sort(courses, Comparator.comparingDouble(Course::getValue).thenComparing(Course::getTitle));

            int i=0;
            while(recommend.size()<10) {
                if(!recommend.contains(courses.get(i).title)) {
                    recommend.add(courses.get(i).title);
                }
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return recommend;
    }


}

class Course {

    String institution;
    String number;
    Date launchDate;
    String title;
    String instructors;
    String subject;
    int year;
    int honorCode;
    int participants;
    int audited;
    int certified;
    double percentAudited;
    double percentCertified;
    double percentCertified50;
    double percentVideo;
    double percentForum;
    double gradeHigherZero;
    double totalHours;
    double medianHoursCertification;
    double medianAge;
    double percentMale;
    double percentFemale;
    double percentDegree;

    double value;
    double sumAge;
    double sumMale;
    double sumDegree;
    double num;
//    double averageAge = sumAge / num;
//    double averageMale = sumMale / num;
//    double aveargeDegree = sumDegree / num;

    public Course(String institution, String number, Date launchDate,
        String title, String instructors, String subject,
        int year, int honorCode, int participants,
        int audited, int certified, double percentAudited,
        double percentCertified, double percentCertified50,
        double percentVideo, double percentForum, double gradeHigherZero,
        double totalHours, double medianHoursCertification,
        double medianAge, double percentMale, double percentFemale,
        double percentDegree) {
        this.institution = institution;
        this.number = number;
        this.launchDate = launchDate;
        if (title.startsWith("\"")) {
            title = title.substring(1);
        }
        if (title.endsWith("\"")) {
            title = title.substring(0, title.length() - 1);
        }
        this.title = title;
        if (instructors.startsWith("\"")) {
            instructors = instructors.substring(1);
        }
        if (instructors.endsWith("\"")) {
            instructors = instructors.substring(0, instructors.length() - 1);
        }
        this.instructors = instructors;
        if (subject.startsWith("\"")) {
            subject = subject.substring(1);
        }
        if (subject.endsWith("\"")) {
            subject = subject.substring(0, subject.length() - 1);
        }
        this.subject = subject;
        this.year = year;
        this.honorCode = honorCode;
        this.participants = participants;
        this.audited = audited;
        this.certified = certified;
        this.percentAudited = percentAudited;
        this.percentCertified = percentCertified;
        this.percentCertified50 = percentCertified50;
        this.percentVideo = percentVideo;
        this.percentForum = percentForum;
        this.gradeHigherZero = gradeHigherZero;
        this.totalHours = totalHours;
        this.medianHoursCertification = medianHoursCertification;
        this.medianAge = medianAge;
        this.percentMale = percentMale;
        this.percentFemale = percentFemale;
        this.percentDegree = percentDegree;
    }

    public double getValue() {
        return value;
    }

    public String getTitle() {
        return title;
    }
}