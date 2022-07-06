# YelpClone
An app that uses Yelp API to return restaurants based on queries!

## Features
- Query the Yelp API using restaurant names as the search term displayed in a RecyclerView
- Examine restaurants through the detail view (makes use of fragments, TabLayout and ViewPager2, and more)
  - Shows an overview and reviews of a selected restaurant
- Search for nearby restaurants from your current location

## Demo
### Search
This function allows you to query a restaurant given a search term. Once results are queried, general information about the restaurant are displayed such as the name, location, price, and category. So as not to stress the API by returning too many query results, the minimum number of characters in the search term before the query is fired was limited to 3. Currently, the location of the restaurants that are queried are hardcoded.

![](https://media.giphy.com/media/Jy1jFzy6u7RVadJGCD/giphy.gif)

### Detail View


![](https://media.giphy.com/media/RwdFk1FEcZer6IkCu0/giphy.gif)

### Nearby Restaurants


![](https://media.giphy.com/media/Qnml7YwLpXENNVm6Gb/giphy.gif)
