from __future__ import with_statement
try:
    import json
except ImportError:
    import simplejson as json
import six
if six.PY3:
    import unittest
else:
    import unittest2 as unittest
from mock import Mock
from twilio.rest.resources import PhoneNumbers
from twilio.rest.resources import PhoneNumber


class PhoneNumberTest(unittest.TestCase):

    def setUp(self):
        self.parent = Mock()
        self.uri = "/base"
        self.auth = ("AC123", "token")

    def test_update_rename_status_callback_url(self):
        mock = Mock()
        mock.uri = "/base"
        instance = PhoneNumber(mock, "SID")
        instance.update(status_callback_url="http://www.example.com")
        mock.update.assert_called_with("SID", status_callback="http://www.example.com")

    def test_update_instance_rename_status_callback_url(self):
        resource = PhoneNumbers(self.uri, self.auth)
        resource.update_instance = Mock()
        resource.update("SID", status_callback_url="http://www.example.com")
        resource.update_instance.assert_called_with("SID", {"status_callback": "http://www.example.com"})

    def test_application_sid(self):
        resource = PhoneNumbers(self.uri, self.auth)
        resource.update_instance = Mock()
        resource.update("SID", application_sid="foo")
        resource.update_instance.assert_called_with(
                "SID", {"voice_application_sid": "foo", "sms_application_sid": "foo"})

    def test_voice_application_sid(self):
        resource = PhoneNumbers(self.uri, self.auth)
        resource.update_instance = Mock()
        resource.update("SID", voice_application_sid="foo")
        resource.update_instance.assert_called_with(
                "SID", {"voice_application_sid": "foo"})

    def test_sms_application_sid(self):
        resource = PhoneNumbers(self.uri, self.auth)
        resource.update_instance = Mock()
        resource.update("SID", sms_application_sid="foo")
        resource.update_instance.assert_called_with(
                "SID", {"sms_application_sid": "foo"})

    def test_status_callback_url(self):
        resource = PhoneNumbers(self.uri, self.auth)
        resource.update_instance = Mock()
        resource.update("SID", status_callback="foo")
        resource.update_instance.assert_called_with(
                "SID", {"status_callback": "foo"})

    def test_transfer(self):
        resource = PhoneNumbers(self.uri, self.auth)
        resource.update = Mock()
        resource.transfer("SID", "AC123")
        resource.update.assert_called_with("SID", account_sid="AC123")

    def test_instance_transfer(self):
        mock = Mock()
        mock.uri = "/base"
        instance = PhoneNumber(mock, "SID")
        instance.transfer("AC123")
        mock.transfer.assert_called_with("SID", "AC123")

    def test_base_uri(self):
        parent = Mock()
        parent.base_uri = ("https://api.twilio.com/2010-04-01/Accounts/"
            "AC123")
        resource = PhoneNumber(parent, "PNd2ae06cced59a5733d2c1c1c69a83a28")

        with open("tests/resources/incoming_phone_numbers_instance.json") as f:
            entry = json.load(f)
            resource.load(entry)

        self.assertEquals(resource.parent.base_uri,
            ("https://api.twilio.com/2010-04-01/Accounts/AC4bf2dafbed59a573"
             "3d2c1c1c69a83a28"))
