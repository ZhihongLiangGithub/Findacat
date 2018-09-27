package edu.gwu.zhihongliang.findacat.model.schema

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import edu.gwu.zhihongliang.findacat.ObjectAsListJsonAdapterFactory
import org.junit.Assert
import org.junit.Test

class PetfinderResponseTest {

    @Test
    fun test() {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory())
                .add(ObjectAsListJsonAdapterFactory())
                .build()
        val adapter = moshi.adapter(PetfinderResponse::class.java)
        val petfinderResponse = adapter.fromJson(json)
        petfinderResponse?.let {
            Assert.assertNotNull(it.petfinder.pets.pet[0].name.t)
        }
    }


    private val json = """
    {
"@encoding": "iso-8859-1",
"@version": "1.0",
"petfinder": {
"@xmlns:xsi": "http://www.w3.org/2001/XMLSchema-instance",
"lastOffset": {
"""" + "\$t" + """": "1"
},
"pets": {
"pet": {
"options": {
"option": [
{
"""" + "\$t" + """": "altered"
},
{
"""" + "\$t" + """": "hasShots"
}
]
},
"status": {
"""" + "\$t" + """": "A"
},
"contact": {
"phone": {
"""" + "\$t" + """": "(877) 688-2965"
},
"state": {
"""" + "\$t" + """": "VA"
},
"address2": {},
"email": {
"""" + "\$t" + """": "awolmutts@hotmail.com"
},
"city": {
"""" + "\$t" + """": "Falls Church"
},
"zip": {
"""" + "\$t" + """": "22046"
},
"fax": {},
"address1": {}
},
"age": {
"""" + "\$t" + """": "Adult"
},
"size": {
"""" + "\$t" + """": "L"
},
"media": {
"photos": {
"photo": [
{
"@size": "pnt",
"""" + "\$t" + """": "http://photos.petfinder.com/photos/pets/27749450/1/?bust=1383401485&width=60&-pnt.jpg",
"@id": "1"
},
{
"@size": "fpm",
"""" + "\$t" + """": "http://photos.petfinder.com/photos/pets/27749450/1/?bust=1383401485&width=95&-fpm.jpg",
"@id": "1"
},
{
"@size": "x",
"""" + "\$t" + """": "http://photos.petfinder.com/photos/pets/27749450/1/?bust=1383401485&width=500&-x.jpg",
"@id": "1"
},
{
"@size": "pn",
"""" + "\$t" + """": "http://photos.petfinder.com/photos/pets/27749450/1/?bust=1383401485&width=300&-pn.jpg",
"@id": "1"
},
{
"@size": "t",
"""" + "\$t" + """": "http://photos.petfinder.com/photos/pets/27749450/1/?bust=1383401485&width=50&-t.jpg",
"@id": "1"
},
{
"@size": "pnt",
"""" + "\$t" + """": "http://photos.petfinder.com/photos/pets/27749450/2/?bust=1383401486&width=60&-pnt.jpg",
"@id": "2"
},
{
"@size": "fpm",
"""" + "\$t" + """": "http://photos.petfinder.com/photos/pets/27749450/2/?bust=1383401486&width=95&-fpm.jpg",
"@id": "2"
},
{
"@size": "x",
"""" + "\$t" + """": "http://photos.petfinder.com/photos/pets/27749450/2/?bust=1383401486&width=500&-x.jpg",
"@id": "2"
},
{
"@size": "pn",
"""" + "\$t" + """": "http://photos.petfinder.com/photos/pets/27749450/2/?bust=1383401486&width=300&-pn.jpg",
"@id": "2"
},
{
"@size": "t",
"""" + "\$t" + """": "http://photos.petfinder.com/photos/pets/27749450/2/?bust=1383401486&width=50&-t.jpg",
"@id": "2"
}
]
}
},
"id": {
"""" + "\$t" + """": "27749450"
},
"shelterPetId": {},
"breeds": {
"breed": {
"""" + "\$t" + """": "Tabby"
}
},
"name": {
"""" + "\$t" + """": "Widget"
},
"sex": {
"""" + "\$t" + """": "F"
},
"description": {
"""" + "\$t" + """": "Widget is a beautiful gray female tabby - born May 2009.  She is a playful kitty - especially loves to play laser light. Widget gets along with other cats, and with dogs.  She is shy at first, but warms up quickly.  Widget was rescued from a shelter in West Virginia.  She is spayed, micro-chipped, and up-to-date on all medical."
},
"mix": {
"""" + "\$t" + """": "no"
},
"shelterId": {
"""" + "\$t" + """": "VA610"
},
"lastUpdate": {
"""" + "\$t" + """": "2013-11-02T14:11:25Z"
},
"animal": {
"""" + "\$t" + """": "Cat"
}
}
},
"header": {
"timestamp": {
"""" + "\$t" + """": "2018-09-26T21:40:07Z"
},
"status": {
"message": {},
"code": {
"""" + "\$t" + """": "100"
}
},
"version": {
"""" + "\$t" + """": "0.1"
}
},
"@xsi:noNamespaceSchemaLocation": "http://api.petfinder.com/schemas/0.9/petfinder.xsd"
}
}
""".trimIndent()
}