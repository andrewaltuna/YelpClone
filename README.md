# YelpClone
An app that uses Yelp API to return restaurants based on queries!

## Features
- Query the Yelp API using a search term to find and display restaurant results in a RecyclerView
- Examine restaurants through their detail view (makes use of fragments, TabLayout and ViewPager2, and more)
  - Shows an overview and reviews of a selected restaurant
- Search for nearby restaurants from your current location and map them out using Google Maps API

## Demo
### Search
This function allows you to query a restaurant given a search term. Once results are queried, general information about the restaurant are displayed such as the name, location, price, and category. So as not to stress the API by returning too many query results, the minimum number of characters in the search term before the query is fired was set to 3. Currently, the location of the restaurants that are queried is hardcoded to "New York".

![](https://media.giphy.com/media/Jy1jFzy6u7RVadJGCD/giphy.gif)

### Detail View
This function allows you to see an overview of the restaurant, as well as their reviews (limited to 3 reviews and selected by the Yelp API). Similar to the search results, the Detail tab displays general information about the restaurant but with their contact number included. Pictures of the restaurant are also displayed and can be swiped through. The Reviews tab shows the reviews as well as details of the user that posted them.

![](https://media.giphy.com/media/RwdFk1FEcZer6IkCu0/giphy.gif)

### Nearby Restaurants
This function allows you to find restaurants near you. If the app granted permission to use location, your current location is found and used to query the Yelp API to return results. These results are then displayed on a map using Google Maps API. Selecting on a map marker will display the restaurant's name, category, distance from you, and its overall rating. Tapping on its dialogue box will bring you to the restaurant's detail view.

![](https://media.giphy.com/media/Qnml7YwLpXENNVm6Gb/giphy.gif)
