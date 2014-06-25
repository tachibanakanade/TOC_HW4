/**
 * ID: F74009020
 * Author: 姚書涵
 * Description: Parsing real estate
 * 				先抓取道路名稱﹑比對道路名是否有符合要求﹑再來比對道路名與交易年月是否有重複﹑最後再從maxCountIndexList(ArrayList)裡取值(對應到road(ArrayList)的Index)
 * 				其中由於一個道路名會有複數個交易年月﹑因此交易年月用二維的ArrayList來存﹑此外一條道路名只會有一個最高成交價與最低成交價﹑因此以一維來存即可
 * 				maxCountIndexList 存有最多交易次數(最多年月)的那條道路名的Index﹑因此當一讀完JSON就可知道哪條道路的交易次數最多﹑不須重新找
 */


import java.net.*;
import java.io.*;
import org.json.*;
import java.util.*;


public class TocHW4 {

	public static void main(String[] args) throws JSONException {
		// TODO Auto-generated method stub
		ArrayList<String> road = new ArrayList<String>();
		ArrayList<Integer> highestPrice = new ArrayList<Integer>();
		ArrayList<Integer> lowestPrice = new ArrayList<Integer>();
		ArrayList<ArrayList<String>> year = new ArrayList<ArrayList<String>>();
		ArrayList<Integer> maxCountIndexList = new ArrayList<Integer>();
		int roadNameIndex, currentPrice, listRowCount = 0, maxCount = 0, preMaxCount;
		String roadName, currentRoad, currentYear;
		boolean first = true;
		
		try 
		{
			URL url = new URL(args[0]);
			URLConnection connect = url.openConnection();
			InputStreamReader isr = new InputStreamReader(connect.getInputStream(), "UTF-8");
			JSONArray jsonRealPrice = new JSONArray(new JSONTokener(isr));
			//JSONArray jsonRealPrice = new JSONArray(new JSONTokener(new FileReader(new File("5385b69de7259bb37d925971.json"))));
			
			for(int i = 0 ; i < jsonRealPrice.length() ; i++)
			{
				first = true;
				// 抓取 "路" "街" "大道" "巷" 以前的字串
				roadName = jsonRealPrice.getJSONObject(i).getString("土地區段位置或建物區門牌");
				currentRoad = "";
				roadNameIndex = -1;
				if(roadName.lastIndexOf("路") != -1)
					roadNameIndex = roadName.lastIndexOf("路");
				else if(roadName.lastIndexOf("街") != -1)
					roadNameIndex = roadName.lastIndexOf("街");
				else if(roadName.lastIndexOf("大道") != -1)
					roadNameIndex = roadName.lastIndexOf("大道");
				else if(roadName.lastIndexOf("巷") != -1)
					roadNameIndex = roadName.lastIndexOf("巷");
				currentRoad = roadName.substring(0, roadNameIndex + 1);
				// xx路﹑xx街﹑xx大道﹑xx巷
				if(currentRoad.contains("路") || currentRoad.contains("街") || currentRoad.contains("大道") || currentRoad.contains("巷"))
				{
					currentYear = "" + jsonRealPrice.getJSONObject(i).getInt("交易年月");
					currentPrice = jsonRealPrice.getJSONObject(i).getInt("總價元");
					if(road.isEmpty())
					{
						road.add(currentRoad);
						year.add(new ArrayList<String>());
						year.get(listRowCount).add(currentYear);
						highestPrice.add(currentPrice);
						lowestPrice.add(currentPrice);
						maxCount = year.get(listRowCount++).size();
					}
					else
					{
						for(int j = 0 ; j < road.size() ; j++) // 檢查 路名 是否有重複
						{
							if(road.get(j).equals(currentRoad)) // 路名重複
							{
								first = false;
								int k;
								for(k = 0 ; k < year.get(j).size() && !year.get(j).get(k).equals(currentYear) ; k++) ; // 判斷 年月 有無重複
								if(k == year.get(j).size()) // 年月沒有重複
								{
									year.get(j).add(currentYear); // 新增新的 年月
									preMaxCount = maxCount;
									maxCount = Math.max(preMaxCount, year.get(j).size()); // 找出最多的交易次數
									if(preMaxCount != maxCount) // 有更大次數
										maxCountIndexList.clear(); // 先清空在加入list
									if(year.get(j).size() == maxCount) // 把擁有最大次數們的index存起來
										maxCountIndexList.add(j);
								}
								// 不論"年月"有無重複 只要是"路名重複"就要比較總價元
								if(currentPrice > highestPrice.get(j))
									highestPrice.set(j, currentPrice);
								if(currentPrice < lowestPrice.get(j))
									lowestPrice.set(j, currentPrice);
							}
						}
						if(first) // 路名沒有重複
						{
							road.add(currentRoad);
							year.add(new ArrayList<String>());
							year.get(listRowCount++).add(currentYear);
							highestPrice.add(currentPrice);
							lowestPrice.add(currentPrice);
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(int i = 0 ; i < maxCountIndexList.size() ; i++)
			System.out.println(road.get(maxCountIndexList.get(i)) + ", 最高成交價: " + highestPrice.get(maxCountIndexList.get(i)) + ", 最低成交價: " + lowestPrice.get(maxCountIndexList.get(i)));
	}
	
}
